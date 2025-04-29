package org.dakhli.elyes.contactapp.it;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.dakhli.elyes.contactapp.domain.contact.Contact;
import org.dakhli.elyes.contactapp.domain.contact.ContactInsertRequest;
import org.dakhli.elyes.contactapp.domain.contact.ContactService;
import org.dakhli.elyes.contactapp.utils.ContactEndpoint;
import org.jooq.DSLContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.dakhli.elyes.contactapp.fixtures.contact.SomeContactInsertRequest.*;
import static org.dakhli.elyes.contactapp.infra.data.jooq.model.tables.Contact.CONTACT;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
public class ContactIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ContactService contactService;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    DSLContext dslContext;

    @BeforeEach
    void setUp() {
        dslContext.delete(CONTACT)
                .execute();
    }

    @Test
    @SneakyThrows
    void should_create_contact() {
        // Given
        ContactInsertRequest contact = aContactInsertRequest();

        // When
        mockMvc.perform(post(ContactEndpoint.ADD.getFullPath())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(contact)))

                // Then
                .andExpect(status().isOk())
                .andExpectAll(
                        jsonPath("$.id").exists(),
                        jsonPath("$.firstName").value(contact.firstName()),
                        jsonPath("$.lastName").value(contact.lastName()),
                        jsonPath("$.tel").value(contact.tel()),
                        jsonPath("$.email").value(contact.email()));

    }

    @Test
    @SneakyThrows
    void should_find_all_contacts() {
        // Given
        List<Contact> expectedContacts = createContacts(aRandomContactInsertRequest(),
                aRandomContactInsertRequest());
        ArrayList<Contact> sortedExprectedContacts = new ArrayList<>(expectedContacts);
        sortedExprectedContacts.sort(Comparator.comparing(Contact::firstName));

        // When
        mockMvc.perform(get(ContactEndpoint.ALL.getFullPath()))

                // Then
                .andExpect(status().isOk())
                .andExpectAll(
                        jsonPath("$").isArray(),
                        jsonPath("$.length()").value(sortedExprectedContacts.size()),
                        jsonPath("$.[0].id").exists(),
                        jsonPath("$.[0].firstName").value(sortedExprectedContacts.get(0).firstName()),
                        jsonPath("$.[0].lastName").value(sortedExprectedContacts.get(0).lastName()),
                        jsonPath("$.[0].tel").value(sortedExprectedContacts.get(0).tel()),
                        jsonPath("$.[0].email").value(sortedExprectedContacts.get(0).email()),
                        jsonPath("$.[1].id").exists(),
                        jsonPath("$.[1].firstName").value(sortedExprectedContacts.get(1).firstName()),
                        jsonPath("$.[1].lastName").value(sortedExprectedContacts.get(1).lastName()),
                        jsonPath("$.[1].tel").value(sortedExprectedContacts.get(1).tel()),
                        jsonPath("$.[1].email").value(sortedExprectedContacts.get(1).email()));

    }

    @Test
    @SneakyThrows
    void should_find_contact_by_last_name() {
        // Given
        var elyes = elyes();
        var wiem = wiem();
        createContacts(elyes, wiem);

        // When
        mockMvc.perform(get(ContactEndpoint.BY_QUERY.getFullPath("lastName={lastName}"), wiem.lastName()))

                // Then
                .andDo(print())
                .andExpect(status().isOk())
                .andExpectAll(
                        jsonPath("$").isArray(),
                        jsonPath("$.length()").value(1),
                        jsonPath("$.[0].id").exists(),
                        jsonPath("$.[0].firstName").value(wiem.firstName()),
                        jsonPath("$.[0].lastName").value(wiem.lastName()),
                        jsonPath("$.[0].tel").value(wiem.tel()),
                        jsonPath("$.[0].email").value(wiem.email()));
    }

    @Test
    @SneakyThrows
    void should_create_many_random_contacts_and_find_them() {
        // Given
        int nbContacts = 1_000;
        log.info("Generating " + nbContacts + " random contacts...");
        List<ContactInsertRequest> randomContacts = generateRandomContactInsertRequests(nbContacts);
        log.info("Saving " + randomContacts.size() + " random contacts...");
        List<Contact> expectedContacts = parallelSaveContactsWithApi(randomContacts);
        log.info("Saved random contacts.");
        // Then
        MvcResult mvcResult = mockMvc.perform(
                        get(ContactEndpoint.ALL.getFullPath("pageSize=" + randomContacts.size())))

                // Then
                .andExpect(status().isOk())
                .andExpectAll(
                        jsonPath("$").isArray(),
                        jsonPath("$.length()").value(nbContacts))
                .andReturn();
        Contact[] actualContactsArray = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Contact[].class);

        Assertions.assertThat(Arrays.asList(actualContactsArray)).containsExactlyInAnyOrderElementsOf(expectedContacts);
    }

    @Test
    @SneakyThrows
    void should_batch_create_many_random_contacts_and_find_them() {
        int nbContacts = 100_000;
        int chunkSize = 10_000;

        int startIndex = 0;
        int endIndex = Math.min(startIndex + chunkSize, nbContacts);

        int pageIndex = 0;
        int totalPages = nbContacts / chunkSize + (nbContacts % chunkSize == 0 ? 0 : 1);

        long startTime = System.currentTimeMillis();

        while (startIndex < nbContacts) {
            log.info("Running page {}/{}.", ++pageIndex, totalPages);
            int currentChunkSize = endIndex - startIndex;

            log.info("Generating " + currentChunkSize + " random contacts...");
            List<ContactInsertRequest> randomContacts = generateRandomContactInsertRequests(currentChunkSize);

            log.info("Saving " + randomContacts.size() + " random contacts...");
            List<Contact> savedContacts = parallelSaveContactsWithApi(randomContacts);
            log.info("Saved random {} contacts.", savedContacts.size());

            startIndex = startIndex + chunkSize;
            endIndex = Math.min(endIndex + chunkSize, nbContacts);
        }
        log.info("Batch completed in {} ms.", System.currentTimeMillis() - startTime);
    }

    @SneakyThrows
    private List<Contact> parallelSaveContactsWithApi(List<ContactInsertRequest> randomContacts) {
        // initialize pool
        int nbProcessors = Runtime.getRuntime().availableProcessors();

        log.info("Available processors: {}", nbProcessors);
        int poolSize = 2 * nbProcessors;
        log.info("Using pool size: {}", poolSize);
        ExecutorService executorService = Executors.newFixedThreadPool(poolSize);

        // Rest calls
        List<CompletableFuture<Contact>> futures = randomContacts
                .stream()
                .map(contIns -> supplyAsyncApiCreate(contIns, executorService))
                .toList();

        // Wait for all futures to complete
        CompletableFuture<Void> allFutures = CompletableFuture.allOf(
                futures.toArray(new CompletableFuture[0])
        );

        // Combine results
        CompletableFuture<List<Contact>> allResults = allFutures.thenApply(v ->
                futures.stream()
                        .map(CompletableFuture::join) // Get the result of each future
                        .toList()
        );

        return allResults.get();
    }

    private CompletableFuture<Contact> supplyAsyncApiCreate(ContactInsertRequest contactInsertRequest, ExecutorService executorService) {
        return CompletableFuture.supplyAsync(() -> peformCreateContactPost(contactInsertRequest), executorService);
    }

    private Contact peformCreateContactPost(ContactInsertRequest contactInsertRequest) {
        try {
            MvcResult mvcResult =
                    mockMvc.perform(post(ContactEndpoint.ADD.getFullPath())
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(contactInsertRequest)))
                            .andExpect(status().isOk())
                            .andReturn();
            return objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Contact.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private List<ContactInsertRequest> generateRandomContactInsertRequests(int n) {
        return IntStream.range(0, n)
                .parallel()
                .mapToObj(i -> aRandomContactInsertRequest())
                .toList();
    }

    private List<Contact> createContacts(ContactInsertRequest... contacts) {
        return Stream.of(contacts)
                .map(contactService::createContact)
                .toList();
    }
}
