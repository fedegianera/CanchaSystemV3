//package com.example.CanchaSystem.config;
//
//import com.example.CanchaSystem.model.*;
//import com.example.CanchaSystem.repository.*;
//import com.github.javafaker.Faker;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.stereotype.Component;
//
//import java.time.LocalTime;
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Random;
//
//@Component
//public class DataLoader implements CommandLineRunner {
//
//    private final RoleRepository roleRepository;
//    private final OwnerRepository ownerRepository;
//    private final CanchaBrandRepository brandRepository;
//    private final EstablishmentRepository establishmentRepository;
//    private final AdminRepository adminRepository;
//    private final ClientRepository clientRepository;
//    private final CanchaRepository canchaRepository;
//    private final ReservationRepository reservationRepository;
//    private final ReviewRepository reviewRepository;
//
//    private final Faker faker = new Faker();
//    private final Random random = new Random();
//
//    public DataLoader(RoleRepository roleRepository,
//                      OwnerRepository ownerRepository,
//                      CanchaBrandRepository brandRepository,
//                      EstablishmentRepository establishmentRepository,
//                      AdminRepository adminRepository,
//                      ClientRepository clientRepository,
//                      CanchaRepository canchaRepository,
//                      ReservationRepository reservationRepository,
//                      ReviewRepository reviewRepository) {
//        this.roleRepository = roleRepository;
//        this.ownerRepository = ownerRepository;
//        this.brandRepository = brandRepository;
//        this.establishmentRepository = establishmentRepository;
//        this.adminRepository = adminRepository;
//        this.clientRepository = clientRepository;
//        this.canchaRepository = canchaRepository;
//        this.reservationRepository = reservationRepository;
//        this.reviewRepository = reviewRepository;
//    }
//
//    @Override
//    public void run(String... args) throws Exception {
//
//        // 1️⃣ Crear Roles solo si no existen
//        Role adminRole = roleRepository.findByName("ADMIN")
//                .orElseGet(() -> roleRepository.save(new Role("ADMIN")));
//        Role clientRole = roleRepository.findByName("CLIENT")
//                .orElseGet(() -> roleRepository.save(new Role("CLIENT")));
//        Role ownerRole = roleRepository.findByName("OWNER")
//                .orElseGet(() -> roleRepository.save(new Role("OWNER")));
//
//        // 2️⃣ Crear Owners
//        List<Owner> owners = new ArrayList<>();
//        for (int i = 0; i < 5; i++) {
//            Owner owner = new Owner();
//            owner.setName(faker.name().firstName());
//            owner.setLastName(faker.name().lastName());
//            owner.setUsername(faker.name().username());
//            owner.setPassword("1234");
//            owner.setMail(faker.internet().emailAddress());
//            owner.setCellNumber(faker.phoneNumber().cellPhone());
//            owner.setRole(ownerRole);
//            owner.setBankOwner(faker.number().randomDouble(2, 1000, 10000));
//            owners.add(ownerRepository.save(owner));
//        }
//
//        // 3️⃣ Crear Brands
//        List<Brand> brands = new ArrayList<>();
//        for (Owner owner : owners) {
//            for (int i = 0; i < 2; i++) {
//                Brand brand = new Brand();
//                brand.setBrandName(faker.company().name());
//                brand.setOwner(owner);
//                brand.setActive(true);
//                brands.add(brandRepository.save(brand));
//            }
//        }
//
//        // 4️⃣ Crear Establishments
//        List<Establishment> establishments = new ArrayList<>();
//        for (Brand brand : brands) {
//            for (int i = 0; i < 2; i++) {
//                Establishment est = new Establishment();
//                est.setName(faker.company().name());
//                est.setBrand(brand);
//                est.setAddress(faker.address().fullAddress());
//                est.setOpeningHour(LocalTime.of(8, 0));
//                est.setClosingHour(LocalTime.of(22, 0));
//                est.setCanShower(faker.bool().bool());
//                est.setActive(true);
//                establishments.add(establishmentRepository.save(est));
//            }
//        }
//
//        // 5️⃣ Crear Canchas
//        List<Cancha> canchas = new ArrayList<>();
//        CanchaType[] canchaTypes = CanchaType.values();
//        for (Establishment est : establishments) {
//            for (int i = 0; i < 3; i++) {
//                Cancha cancha = new Cancha();
//                cancha.setTotalAmount(faker.number().randomDouble(2, 1000, 5000));
//                cancha.setActive(true);
//                cancha.setHasRoof(faker.bool().bool());
//                cancha.setEstablishment(est);
//                cancha.setCanchaType(canchaTypes[random.nextInt(canchaTypes.length)]);
//                cancha.setWorking(faker.bool().bool());
//                canchas.add(canchaRepository.save(cancha));
//            }
//        }
//
//        // 6️⃣ Crear Admins
//        for (int i = 0; i < 3; i++) {
//            Admin admin = new Admin();
//            admin.setUsername("admin" + i);
//            admin.setPassword("1234");
//            admin.setRole(adminRole);
//            adminRepository.save(admin);
//        }
//
//        // 7️⃣ Crear Clients
//        List<Client> clients = new ArrayList<>();
//        for (int i = 0; i < 10; i++) {
//            Client client = new Client();
//            client.setName(faker.name().firstName());
//            client.setLastName(faker.name().lastName());
//            client.setUsername(faker.name().username());
//            client.setPassword("1234");
//            client.setMail(faker.internet().emailAddress());
//            client.setCellNumber(faker.phoneNumber().cellPhone());
//            client.setRole(clientRole);
//            client.setBankClient(faker.number().randomDouble(2, 0, 1000));
//            client.setActive(true);
//            clients.add(clientRepository.save(client));
//        }
//
//        // 8️⃣ Crear Reservations
//        ReservationStatus[] statuses = ReservationStatus.values();
//        for (int i = 0; i < 20; i++) {
//            Reservation res = new Reservation();
//            res.setClient(clients.get(random.nextInt(clients.size())));
//            res.setCancha(canchas.get(random.nextInt(canchas.size())));
//            res.setReservationDate(LocalDateTime.now().minusDays(faker.number().numberBetween(0, 10)));
//            res.setMatchDate(LocalDateTime.now().plusDays(faker.number().numberBetween(1, 20)));
//            res.setDeposit(faker.number().randomDouble(2, 50, 500));
//            res.setStatus(statuses[random.nextInt(statuses.length)]);
//            reservationRepository.save(res);
//        }
//
//        // 9️⃣ Crear Reviews
//        for (int i = 0; i < 15; i++) {
//            Review review = new Review();
//            review.setClient(clients.get(random.nextInt(clients.size())));
//            review.setCancha(canchas.get(random.nextInt(canchas.size())));
//            review.setRating(faker.number().randomDouble(1, 1, 5));
//            review.setMessage(faker.lorem().sentence());
//            review.setActive(true);
//            reviewRepository.save(review);
//        }
//
//        System.out.println("✅ Datos de prueba generados con Faker");
//    }
//}
