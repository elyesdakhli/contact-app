package org.dakhli.elyes.contactapp.domain.contact.impl;

import com.github.javafaker.Faker;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.dakhli.elyes.contactapp.domain.contact.Contact;
import org.dakhli.elyes.contactapp.domain.contact.ContactInsertRequest;
import org.dakhli.elyes.contactapp.domain.contact.ContactService;
import org.dakhli.elyes.contactapp.domain.contact.RandomContactsGenerator;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

@Slf4j
@Service
public class ConcurrentRandomContactsGenerator implements RandomContactsGenerator {
    private static final int DEFAULT_CHUNK_SIZE = 10_000;
    private static final int MAX_POOL_SIZE = 1_000;
    private final Faker faker = new Faker();
    private final ContactService contactService;

    public ConcurrentRandomContactsGenerator(ContactService contactService) {
        this.contactService = contactService;
    }


    @Override
    public int generate(int nbContacts) {
        int chunkSize = DEFAULT_CHUNK_SIZE;
        int poolSize = Math.min(chunkSize, MAX_POOL_SIZE);

        int startIndex = 0;
        int endIndex = Math.min(startIndex + chunkSize, nbContacts);

        int pageIndex = 0;
        int totalPages = nbContacts / chunkSize + (nbContacts % chunkSize == 0 ? 0 : 1);

        long startTime = System.currentTimeMillis();
        int totalGenerated = 0;

        while (startIndex < nbContacts) {
            log.info("Running page {}/{}.", ++pageIndex, totalPages);
            int currentChunkSize = endIndex - startIndex;

            log.info("Generating {} random contacts...", currentChunkSize);
            List<ContactInsertRequest> randomContacts = generateRandomContactInsertRequests(currentChunkSize);

            log.info("Saving {} random contacts...", randomContacts.size());
            List<Contact> savedContacts = parallelSaveContactsWithApi(randomContacts, poolSize);
            log.info("Saved random {} contacts.", savedContacts.size());

            totalGenerated += savedContacts.size();

            startIndex = startIndex + chunkSize;
            endIndex = Math.min(endIndex + chunkSize, nbContacts);
        }
        log.info("Batch completed in {} ms.", System.currentTimeMillis() - startTime);
        return totalGenerated;
    }

    private List<ContactInsertRequest> generateRandomContactInsertRequests(int n) {
        return IntStream.range(0, n)
                .parallel()
                .mapToObj(i -> aRandomContactInsertRequest())
                .toList();
    }

    private ContactInsertRequest aRandomContactInsertRequest() {
        var firstName = faker.name().firstName();
        var lastName = faker.name().lastName();
        var tel = faker.phoneNumber().phoneNumber();
        var email = faker.internet().emailAddress();
        return new ContactInsertRequest(firstName, lastName, tel, email);
    }

    @SneakyThrows
    private List<Contact> parallelSaveContactsWithApi(List<ContactInsertRequest> randomContacts, int poolSize) {
        // initialize pool
        int calculatedPoolSize = Math.min(randomContacts.size(), poolSize);
        ExecutorService executorService = Executors.newFixedThreadPool(calculatedPoolSize);

        // Rest calls
        List<CompletableFuture<Contact>> futures = randomContacts
                .stream()
                .map(contIns -> callCreateApiAsync(contIns, executorService))
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

    private CompletableFuture<Contact> callCreateApiAsync(ContactInsertRequest contactInsertRequest, ExecutorService executorService) {
        return CompletableFuture.supplyAsync(() ->
                        contactService.createContact(contactInsertRequest),
                executorService);
    }
}
