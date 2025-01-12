package ru.modgy.pet.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import ru.modgy.exception.NotFoundException;
import ru.modgy.owner.dto.OwnerShortDto;
import ru.modgy.owner.dto.mapper.OwnerMapper;
import ru.modgy.owner.model.Owner;
import ru.modgy.pet.dto.NewPetDto;
import ru.modgy.pet.dto.PetDto;
import ru.modgy.pet.dto.UpdatePetDto;
import ru.modgy.pet.mapper.PetMapper;
import ru.modgy.pet.model.Pet;
import ru.modgy.pet.model.Sex;
import ru.modgy.pet.model.TypeOfPet;
import ru.modgy.pet.repository.PetRepository;
import ru.modgy.user.model.Roles;
import ru.modgy.user.model.User;
import ru.modgy.utility.EntityService;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class PetServiceImplTest {

    private static final LocalDate BIRTH_DATE = LocalDate.now().minusYears(1);
    private static final LocalDate VET_VISIT_DATE = LocalDate.now().minusMonths(1);
    private static final LocalDate HEAT_DATE = LocalDate.now().plusMonths(1);
    private final LocalDateTime registrationDate = LocalDateTime.now();

    @InjectMocks
    private PetServiceImpl petService;

    @Mock
    private PetRepository mockPetRepository;

    @Mock
    private PetMapper mockPetMapper;

    @Mock
    private EntityService entityService;

    @Mock
    private OwnerMapper ownerMapper;

    private final Owner owner = Owner.builder()
            .id(1L)
            .firstName("Ivan")
            .lastName("Ivanov")
            .middleName("Ivanovich")
            .mainPhone("89000000000")
            .optionalPhone("89000000001")
            .otherContacts("other contacts")
            .actualAddress("actual address")
            .trustedMan("trusted man")
            .source("source")
            .comment("comment")
            .rating(5)
            .registrationDate(registrationDate)
            .build();

    private final OwnerShortDto ownerShortDto = OwnerShortDto.builder()
            .id(1L)
            .firstName("Ivan")
            .lastName("Ivanov")
            .middleName("Ivanovich")
            .mainPhone("89000000000")
            .optionalPhone("89000000001")
            .build();

    final User requesterBoss = User.builder()
            .email("boss@mail.ru")
            .firstName("boss")
            .id(1L)
            .role(Roles.ROLE_BOSS)
            .build();

    final User requesterAdmin = User.builder()
            .email("admin@mail.ru")
            .firstName("admin")
            .id(2L)
            .role(Roles.ROLE_ADMIN)
            .build();

    final User requesterUser = User.builder()
            .email("user@mail.ru")
            .firstName("user")
            .id(3L)
            .role(Roles.ROLE_USER)
            .build();

    final NewPetDto newPetDto = NewPetDto.builder()
            .type(TypeOfPet.DOG)
            .name("Шарик")
            .breed("Spaniel")
            .birthDate(BIRTH_DATE)
            .sex(Sex.FEMALE)
            .color("black")
            .sign("Клеймо")
            .isExhibition(true)
            .vetVisitDate(VET_VISIT_DATE)
            .vetVisitReason("Ушиб")
            .vaccine("22.11.2023 антиклещ")
            .parasites("22.11.2023")
            .fleaMite("22.11.2023 антиклещ")
            .surgery("Кастрация")
            .pastDisease("пиелонефрит")
            .healthCharacteristic("без особенностей")
            .urineAnalysis("1.11.2023")
            .isAllergy(true)
            .allergyType("На молоко")
            .isChronicDisease(true)
            .chronicDiseaseType("Пиелонефрит")
            .heatDate(HEAT_DATE)
            .vetData("89000000000 Иван Иваныч Иванов")
            .stayWithoutMaster("Да, разлучается спокойно")
            .stayAlone("нет, воет, когда остаётся один")
            .specialCare("да, нужно расчёсывать шерсть два раза в день")
            .barkHowl("да")
            .furnitureDamage("нет")
            .foodFromTable("нет")
            .defecateAtHome("нет")
            .markAtHome("нет")
            .newPeople("дружелюбно")
            .isBitePeople(true)
            .reasonOfBite("Дразнил")
            .playWithDogs("да")
            .isDogTraining(true)
            .trainingName("Послушная собака")
            .like("Когда гладят по голове")
            .notLike("Когда трогают хвост")
            .toys("Мячик")
            .badHabit("Разбрасывает еду из миски")
            .walking("3 раза")
            .morningWalking("В 6 утра")
            .dayWalking("В 2 часа дня")
            .eveningWalking("В 8 часов вечера")
            .feedingQuantity(7)
            .feedType("сухой корм")
            .feedName("Довольный пёс")
            .feedComposition("говядина и овощи")
            .feedingRate("100 г")
            .feedingPractice("без особенностей")
            .treat("сахар")
            .isMedicine(true)
            .medicineRegimen("Здоровая собака")
            .additionalData("Любит, чтоб чесали животик")
            .build();

    final PetDto petDto = PetDto.builder()
            .id(1L)
            .type(TypeOfPet.DOG)
            .name("Шарик")
            .breed("Spaniel")
            .birthDate(BIRTH_DATE)
            .sex(Sex.FEMALE)
            .color("black")
            .sign("Клеймо")
            .isExhibition(true)
            .vetVisitDate(VET_VISIT_DATE)
            .vetVisitReason("Ушиб")
            .vaccine("22.11.2023 антиклещ")
            .parasites("22.11.2023")
            .fleaMite("22.11.2023 антиклещ")
            .surgery("Кастрация")
            .pastDisease("пиелонефрит")
            .healthCharacteristic("без особенностей")
            .urineAnalysis("1.11.2023")
            .isAllergy(true)
            .allergyType("На молоко")
            .isChronicDisease(true)
            .chronicDiseaseType("Пиелонефрит")
            .heatDate(HEAT_DATE)
            .vetData("89000000000 Иван Иваныч Иванов")
            .stayWithoutMaster("Да, разлучается спокойно")
            .stayAlone("нет, воет, когда остаётся один")
            .specialCare("да, нужно расчёсывать шерсть два раза в день")
            .barkHowl("да")
            .furnitureDamage("нет")
            .foodFromTable("нет")
            .defecateAtHome("нет")
            .markAtHome("нет")
            .newPeople("дружелюбно")
            .isBitePeople(true)
            .reasonOfBite("Дразнил")
            .playWithDogs("да")
            .isDogTraining(true)
            .trainingName("Послушная собака")
            .like("Когда гладят по голове")
            .notLike("Когда трогают хвост")
            .toys("Мячик")
            .badHabit("Разбрасывает еду из миски")
            .walking("3 раза")
            .morningWalking("В 6 утра")
            .dayWalking("В 2 часа дня")
            .eveningWalking("В 8 часов вечера")
            .feedingQuantity(7)
            .feedType("сухой корм")
            .feedName("Довольный пёс")
            .feedComposition("говядина и овощи")
            .feedingRate("100 г")
            .feedingPractice("без особенностей")
            .treat("сахар")
            .isMedicine(true)
            .medicineRegimen("Здоровая собака")
            .additionalData("Любит, чтоб чесали животик")
            .build();

    final Pet pet = Pet.builder()
            .id(1L)
            .owner(owner)
            .type(TypeOfPet.DOG)
            .name("Шарик")
            .breed("Spaniel")
            .birthDate(BIRTH_DATE)
            .sex(Sex.FEMALE)
            .color("black")
            .sign("Клеймо")
            .isExhibition(true)
            .vetVisitDate(VET_VISIT_DATE)
            .vetVisitReason("Ушиб")
            .vaccine("22.11.2023 антиклещ")
            .parasites("22.11.2023")
            .fleaMite("22.11.2023 антиклещ")
            .surgery("Кастрация")
            .pastDisease("пиелонефрит")
            .healthCharacteristic("без особенностей")
            .urineAnalysis("1.11.2023")
            .isAllergy(true)
            .allergyType("На молоко")
            .isChronicDisease(true)
            .chronicDiseaseType("Пиелонефрит")
            .heatDate(HEAT_DATE)
            .vetData("89000000000 Иван Иваныч Иванов")
            .stayWithoutMaster("Да, разлучается спокойно")
            .stayAlone("нет, воет, когда остаётся один")
            .specialCare("да, нужно расчёсывать шерсть два раза в день")
            .barkHowl("да")
            .furnitureDamage("нет")
            .foodFromTable("нет")
            .defecateAtHome("нет")
            .markAtHome("нет")
            .newPeople("дружелюбно")
            .isBitePeople(true)
            .reasonOfBite("Дразнил")
            .playWithDogs("да")
            .isDogTraining(true)
            .trainingName("Послушная собака")
            .like("Когда гладят по голове")
            .notLike("Когда трогают хвост")
            .toys("Мячик")
            .badHabit("Разбрасывает еду из миски")
            .walking("3 раза")
            .morningWalking("В 6 утра")
            .dayWalking("В 2 часа дня")
            .eveningWalking("В 8 часов вечера")
            .feedingQuantity(7)
            .feedType("сухой корм")
            .feedName("Довольный пёс")
            .feedComposition("говядина и овощи")
            .feedingRate("100 г")
            .feedingPractice("без особенностей")
            .treat("сахар")
            .isMedicine(true)
            .medicineRegimen("Здоровая собака")
            .additionalData("Любит, чтоб чесали животик")
            .build();

    final UpdatePetDto updatePetDto = UpdatePetDto.builder()
            .type(TypeOfPet.CAT)
            .name("Шар")
            .breed("Span")
            .birthDate(BIRTH_DATE.minusYears(1))
            .sex(Sex.MALE)
            .color("red")
            .sign("Чип")
            .isExhibition(false)
            .vetVisitDate(VET_VISIT_DATE.minusMonths(1))
            .vetVisitReason("Укус")
            .vaccine("22.09.2023 антиклещ")
            .parasites("22.09.2023")
            .fleaMite("22.09.2023 антиклещ")
            .surgery("Удаление бородавки")
            .pastDisease("отит")
            .healthCharacteristic("без особенностей")
            .urineAnalysis("1.09.2023")
            .isAllergy(true)
            .allergyType("На орехи")
            .isChronicDisease(true)
            .chronicDiseaseType("Гломерулонефрит")
            .heatDate(HEAT_DATE.plusDays(10))
            .vetData("89000000015 Иван Иваныч Иванов")
            .stayWithoutMaster("разлучается неспокойно")
            .stayAlone("да, ведёт себя спокойно, когда остаётся один")
            .specialCare("нет")
            .barkHowl("нет")
            .furnitureDamage("да")
            .foodFromTable("да")
            .defecateAtHome("да")
            .markAtHome("да")
            .newPeople("недружелюбно")
            .isBitePeople(true)
            .reasonOfBite("Дразнила")
            .playWithDogs("нет")
            .isDogTraining(true)
            .trainingName("Послушная кошка")
            .like("Когда гладят по спине")
            .notLike("Когда трогают уши")
            .toys("Мышка")
            .badHabit("Разбрасывает наполнитель из лотка")
            .walking("4 раз")
            .morningWalking("В 7 утра")
            .dayWalking("В 2 и 4 часа дня")
            .eveningWalking("В 9 часов вечера")
            .feedingQuantity(6)
            .feedType("консервы")
            .feedName("Довольный кот")
            .feedComposition("рыба")
            .feedingRate("10 г")
            .feedingPractice("нужно погреть корм")
            .treat("сметана")
            .isMedicine(true)
            .medicineRegimen("Здоровая кошка")
            .additionalData("Любит, чтоб чесали за ушком")
            .build();

    final Pet updatePet = Pet.builder()
            .id(1L)
            .type(TypeOfPet.CAT)
            .name("Шар")
            .breed("Span")
            .birthDate(BIRTH_DATE.minusYears(1))
            .sex(Sex.MALE)
            .color("red")
            .sign("Чип")
            .isExhibition(false)
            .vetVisitDate(VET_VISIT_DATE.minusMonths(1))
            .vetVisitReason("Укус")
            .vaccine("22.09.2023 антиклещ")
            .parasites("22.09.2023")
            .fleaMite("22.09.2023 антиклещ")
            .surgery("Удаление бородавки")
            .pastDisease("отит")
            .healthCharacteristic("без особенностей")
            .urineAnalysis("1.09.2023")
            .isAllergy(true)
            .allergyType("На орехи")
            .isChronicDisease(true)
            .chronicDiseaseType("Гломерулонефрит")
            .heatDate(HEAT_DATE.plusDays(10))
            .vetData("89000000015 Иван Иваныч Иванов")
            .stayWithoutMaster("разлучается неспокойно")
            .stayAlone("да, ведёт себя спокойно, когда остаётся один")
            .specialCare("нет")
            .barkHowl("нет")
            .furnitureDamage("да")
            .foodFromTable("да")
            .defecateAtHome("да")
            .markAtHome("да")
            .newPeople("недружелюбно")
            .isBitePeople(true)
            .reasonOfBite("Дразнила")
            .playWithDogs("нет")
            .isDogTraining(true)
            .trainingName("Послушная кошка")
            .like("Когда гладят по спине")
            .notLike("Когда трогают уши")
            .toys("Мышка")
            .badHabit("Разбрасывает наполнитель из лотка")
            .walking("4 раз")
            .morningWalking("В 7 утра")
            .dayWalking("В 2 и 4 часа дня")
            .eveningWalking("В 9 часов вечера")
            .feedingQuantity(6)
            .feedType("консервы")
            .feedName("Довольный кот")
            .feedComposition("рыба")
            .feedingRate("10 г")
            .feedingPractice("нужно погреть корм")
            .treat("сметана")
            .isMedicine(true)
            .medicineRegimen("Здоровая кошка")
            .additionalData("Любит, чтоб чесали за ушком")
            .build();

    final PetDto updatedPetDto = PetDto.builder()
            .id(1L)
            .type(TypeOfPet.CAT)
            .name("Шар")
            .breed("Span")
            .birthDate(BIRTH_DATE.minusYears(1))
            .sex(Sex.MALE)
            .color("red")
            .sign("Чип")
            .isExhibition(false)
            .vetVisitDate(VET_VISIT_DATE.minusMonths(1))
            .vetVisitReason("Укус")
            .vaccine("22.09.2023 антиклещ")
            .parasites("22.09.2023")
            .fleaMite("22.09.2023 антиклещ")
            .surgery("Удаление бородавки")
            .pastDisease("отит")
            .healthCharacteristic("без особенностей")
            .urineAnalysis("1.09.2023")
            .isAllergy(true)
            .allergyType("На орехи")
            .isChronicDisease(true)
            .chronicDiseaseType("Гломерулонефрит")
            .heatDate(HEAT_DATE.plusDays(10))
            .vetData("89000000015 Иван Иваныч Иванов")
            .stayWithoutMaster("разлучается неспокойно")
            .stayAlone("да, ведёт себя спокойно, когда остаётся один")
            .specialCare("нет")
            .barkHowl("нет")
            .furnitureDamage("да")
            .foodFromTable("да")
            .defecateAtHome("да")
            .markAtHome("да")
            .newPeople("недружелюбно")
            .isBitePeople(true)
            .reasonOfBite("Дразнила")
            .playWithDogs("нет")
            .isDogTraining(true)
            .trainingName("Послушная кошка")
            .like("Когда гладят по спине")
            .notLike("Когда трогают уши")
            .toys("Мышка")
            .badHabit("Разбрасывает наполнитель из лотка")
            .walking("4 раз")
            .morningWalking("В 7 утра")
            .dayWalking("В 2 и 4 часа дня")
            .eveningWalking("В 9 часов вечера")
            .feedingQuantity(6)
            .feedType("консервы")
            .feedName("Довольный кот")
            .feedComposition("рыба")
            .feedingRate("10 г")
            .feedingPractice("нужно погреть корм")
            .treat("сметана")
            .isMedicine(true)
            .medicineRegimen("Здоровая кошка")
            .additionalData("Любит, чтоб чесали за ушком")
            .build();

    @Test
    void addPet_whenAddPetByAdmin_thenPetAdded() {
        when(entityService.getUserIfExists(requesterAdmin.getId())).thenReturn(requesterAdmin);
        when(mockPetMapper.toPet(newPetDto)).thenReturn(pet);
        when(mockPetRepository.save(any())).thenReturn(pet);
        when(mockPetMapper.toPetDto(pet)).thenReturn(petDto);
        when(ownerMapper.toOwnerShortDto(any(Owner.class))).thenReturn(ownerShortDto);

        PetDto actualPetDto = petService.addPet(requesterAdmin.getId(), newPetDto);

        assertNotNull(actualPetDto);
        assertThat(actualPetDto.getId(), equalTo(pet.getId()));
        assertThat(actualPetDto.getType(), equalTo(petDto.getType()));
        assertThat(actualPetDto.getName(), equalTo(petDto.getName()));
        assertThat(actualPetDto.getBreed(), equalTo(petDto.getBreed()));
        assertThat(actualPetDto.getBirthDate().isEqual(petDto.getBirthDate()), is(true));
        assertThat(actualPetDto.getAge(), equalTo(petDto.getAge()));
        assertThat(actualPetDto.getSex(), equalTo(petDto.getSex()));
        assertThat(actualPetDto.getColor(), equalTo(petDto.getColor()));
        assertThat(actualPetDto.getSign(), equalTo(petDto.getSign()));
        assertThat(actualPetDto.getIsExhibition(), equalTo(petDto.getIsExhibition()));
        assertThat(actualPetDto.getVetVisitReason(), equalTo(petDto.getVetVisitReason()));
        assertThat(actualPetDto.getVaccine(), equalTo(petDto.getVaccine()));
        assertThat(actualPetDto.getParasites(), equalTo(petDto.getParasites()));
        assertThat(actualPetDto.getFleaMite(), equalTo(petDto.getFleaMite()));
        assertThat(actualPetDto.getSurgery(), equalTo(petDto.getSurgery()));
        assertThat(actualPetDto.getPastDisease(), equalTo(petDto.getPastDisease()));
        assertThat(actualPetDto.getHealthCharacteristic(), equalTo(petDto.getHealthCharacteristic()));
        assertThat(actualPetDto.getUrineAnalysis(), equalTo(petDto.getUrineAnalysis()));
        assertThat(actualPetDto.getIsAllergy(), equalTo(petDto.getIsAllergy()));
        assertThat(actualPetDto.getAllergyType(), equalTo(petDto.getAllergyType()));
        assertThat(actualPetDto.getIsChronicDisease(), equalTo(petDto.getIsChronicDisease()));
        assertThat(actualPetDto.getChronicDiseaseType(), equalTo(petDto.getChronicDiseaseType()));
        assertThat(actualPetDto.getHeatDate(), equalTo(petDto.getHeatDate()));
        assertThat(actualPetDto.getVetData(), equalTo(petDto.getVetData()));
        assertThat(actualPetDto.getStayWithoutMaster(), equalTo(petDto.getStayWithoutMaster()));
        assertThat(actualPetDto.getStayAlone(), equalTo(petDto.getStayAlone()));
        assertThat(actualPetDto.getSpecialCare(), equalTo(petDto.getSpecialCare()));
        assertThat(actualPetDto.getBarkHowl(), equalTo(petDto.getBarkHowl()));
        assertThat(actualPetDto.getFurnitureDamage(), equalTo(petDto.getFurnitureDamage()));
        assertThat(actualPetDto.getFoodFromTable(), equalTo(petDto.getFoodFromTable()));
        assertThat(actualPetDto.getDefecateAtHome(), equalTo(petDto.getDefecateAtHome()));
        assertThat(actualPetDto.getMarkAtHome(), equalTo(petDto.getMarkAtHome()));
        assertThat(actualPetDto.getNewPeople(), equalTo(petDto.getNewPeople()));
        assertThat(actualPetDto.getIsBitePeople(), equalTo(petDto.getIsBitePeople()));
        assertThat(actualPetDto.getReasonOfBite(), equalTo(petDto.getReasonOfBite()));
        assertThat(actualPetDto.getPlayWithDogs(), equalTo(petDto.getPlayWithDogs()));
        assertThat(actualPetDto.getIsDogTraining(), equalTo(petDto.getIsDogTraining()));
        assertThat(actualPetDto.getTrainingName(), equalTo(petDto.getTrainingName()));
        assertThat(actualPetDto.getLike(), equalTo(petDto.getLike()));
        assertThat(actualPetDto.getNotLike(), equalTo(petDto.getNotLike()));
        assertThat(actualPetDto.getToys(), equalTo(petDto.getToys()));
        assertThat(actualPetDto.getBadHabit(), equalTo(petDto.getBadHabit()));
        assertThat(actualPetDto.getWalking(), equalTo(petDto.getWalking()));
        assertThat(actualPetDto.getMorningWalking(), equalTo(petDto.getMorningWalking()));
        assertThat(actualPetDto.getDayWalking(), equalTo(petDto.getDayWalking()));
        assertThat(actualPetDto.getEveningWalking(), equalTo(petDto.getEveningWalking()));
        assertThat(actualPetDto.getFeedingQuantity(), equalTo(petDto.getFeedingQuantity()));
        assertThat(actualPetDto.getFeedType(), equalTo(petDto.getFeedType()));
        assertThat(actualPetDto.getFeedName(), equalTo(petDto.getFeedName()));
        assertThat(actualPetDto.getFeedComposition(), equalTo(petDto.getFeedComposition()));
        assertThat(actualPetDto.getFeedingRate(), equalTo(petDto.getFeedingRate()));
        assertThat(actualPetDto.getFeedingPractice(), equalTo(petDto.getFeedingPractice()));
        assertThat(actualPetDto.getTreat(), equalTo(petDto.getTreat()));
        assertThat(actualPetDto.getIsMedicine(), equalTo(petDto.getIsMedicine()));
        assertThat(actualPetDto.getMedicineRegimen(), equalTo(petDto.getMedicineRegimen()));
        assertThat(actualPetDto.getAdditionalData(), equalTo(petDto.getAdditionalData()));
        verify(mockPetRepository, times(1)).save(any());
    }

    @Test
    void addPet_whenAddPetByBoss_thenPetAdded() {
        when(entityService.getUserIfExists(requesterBoss.getId())).thenReturn(requesterBoss);
        when(mockPetMapper.toPet(newPetDto)).thenReturn(pet);
        when(mockPetRepository.save(any())).thenReturn(pet);
        when(mockPetMapper.toPetDto(pet)).thenReturn(petDto);

        PetDto actualPetDto = petService.addPet(requesterBoss.getId(), newPetDto);

        assertNotNull(actualPetDto);
        assertThat(actualPetDto.getId(), equalTo(pet.getId()));
        assertThat(actualPetDto.getType(), equalTo(petDto.getType()));
        assertThat(actualPetDto.getName(), equalTo(petDto.getName()));
        assertThat(actualPetDto.getBreed(), equalTo(petDto.getBreed()));
        assertThat(actualPetDto.getBirthDate().isEqual(petDto.getBirthDate()), is(true));
        assertThat(actualPetDto.getAge(), equalTo(petDto.getAge()));
        assertThat(actualPetDto.getSex(), equalTo(petDto.getSex()));
        assertThat(actualPetDto.getColor(), equalTo(petDto.getColor()));
        assertThat(actualPetDto.getSign(), equalTo(petDto.getSign()));
        assertThat(actualPetDto.getIsExhibition(), equalTo(petDto.getIsExhibition()));
        assertThat(actualPetDto.getVetVisitReason(), equalTo(petDto.getVetVisitReason()));
        assertThat(actualPetDto.getVaccine(), equalTo(petDto.getVaccine()));
        assertThat(actualPetDto.getParasites(), equalTo(petDto.getParasites()));
        assertThat(actualPetDto.getFleaMite(), equalTo(petDto.getFleaMite()));
        assertThat(actualPetDto.getSurgery(), equalTo(petDto.getSurgery()));
        assertThat(actualPetDto.getPastDisease(), equalTo(petDto.getPastDisease()));
        assertThat(actualPetDto.getHealthCharacteristic(), equalTo(petDto.getHealthCharacteristic()));
        assertThat(actualPetDto.getUrineAnalysis(), equalTo(petDto.getUrineAnalysis()));
        assertThat(actualPetDto.getIsAllergy(), equalTo(petDto.getIsAllergy()));
        assertThat(actualPetDto.getAllergyType(), equalTo(petDto.getAllergyType()));
        assertThat(actualPetDto.getIsChronicDisease(), equalTo(petDto.getIsChronicDisease()));
        assertThat(actualPetDto.getChronicDiseaseType(), equalTo(petDto.getChronicDiseaseType()));
        assertThat(actualPetDto.getHeatDate(), equalTo(petDto.getHeatDate()));
        assertThat(actualPetDto.getVetData(), equalTo(petDto.getVetData()));
        assertThat(actualPetDto.getStayWithoutMaster(), equalTo(petDto.getStayWithoutMaster()));
        assertThat(actualPetDto.getStayAlone(), equalTo(petDto.getStayAlone()));
        assertThat(actualPetDto.getSpecialCare(), equalTo(petDto.getSpecialCare()));
        assertThat(actualPetDto.getBarkHowl(), equalTo(petDto.getBarkHowl()));
        assertThat(actualPetDto.getFurnitureDamage(), equalTo(petDto.getFurnitureDamage()));
        assertThat(actualPetDto.getFoodFromTable(), equalTo(petDto.getFoodFromTable()));
        assertThat(actualPetDto.getDefecateAtHome(), equalTo(petDto.getDefecateAtHome()));
        assertThat(actualPetDto.getMarkAtHome(), equalTo(petDto.getMarkAtHome()));
        assertThat(actualPetDto.getNewPeople(), equalTo(petDto.getNewPeople()));
        assertThat(actualPetDto.getIsBitePeople(), equalTo(petDto.getIsBitePeople()));
        assertThat(actualPetDto.getReasonOfBite(), equalTo(petDto.getReasonOfBite()));
        assertThat(actualPetDto.getPlayWithDogs(), equalTo(petDto.getPlayWithDogs()));
        assertThat(actualPetDto.getIsDogTraining(), equalTo(petDto.getIsDogTraining()));
        assertThat(actualPetDto.getTrainingName(), equalTo(petDto.getTrainingName()));
        assertThat(actualPetDto.getLike(), equalTo(petDto.getLike()));
        assertThat(actualPetDto.getNotLike(), equalTo(petDto.getNotLike()));
        assertThat(actualPetDto.getToys(), equalTo(petDto.getToys()));
        assertThat(actualPetDto.getBadHabit(), equalTo(petDto.getBadHabit()));
        assertThat(actualPetDto.getWalking(), equalTo(petDto.getWalking()));
        assertThat(actualPetDto.getMorningWalking(), equalTo(petDto.getMorningWalking()));
        assertThat(actualPetDto.getDayWalking(), equalTo(petDto.getDayWalking()));
        assertThat(actualPetDto.getEveningWalking(), equalTo(petDto.getEveningWalking()));
        assertThat(actualPetDto.getFeedingQuantity(), equalTo(petDto.getFeedingQuantity()));
        assertThat(actualPetDto.getFeedType(), equalTo(petDto.getFeedType()));
        assertThat(actualPetDto.getFeedName(), equalTo(petDto.getFeedName()));
        assertThat(actualPetDto.getFeedComposition(), equalTo(petDto.getFeedComposition()));
        assertThat(actualPetDto.getFeedingRate(), equalTo(petDto.getFeedingRate()));
        assertThat(actualPetDto.getFeedingPractice(), equalTo(petDto.getFeedingPractice()));
        assertThat(actualPetDto.getTreat(), equalTo(petDto.getTreat()));
        assertThat(actualPetDto.getIsMedicine(), equalTo(petDto.getIsMedicine()));
        assertThat(actualPetDto.getMedicineRegimen(), equalTo(petDto.getMedicineRegimen()));
        assertThat(actualPetDto.getAdditionalData(), equalTo(petDto.getAdditionalData()));
        verify(mockPetRepository, times(1)).save(any());
    }

    @Test
    void getPetById_whenGetPetByUser_thenReturnPetDto() {
        when(entityService.getUserIfExists(requesterUser.getId())).thenReturn(requesterUser);
        when(entityService.getPetIfExists(anyLong())).thenReturn(pet);
        when(mockPetMapper.toPetDto(pet)).thenReturn(petDto);

        PetDto actualPetDto = petService.getPetById(requesterUser.getId(), pet.getId());

        assertNotNull(actualPetDto);
        assertThat(actualPetDto.getId(), equalTo(pet.getId()));
        assertThat(actualPetDto.getType(), equalTo(petDto.getType()));
        assertThat(actualPetDto.getName(), equalTo(petDto.getName()));
        assertThat(actualPetDto.getBreed(), equalTo(petDto.getBreed()));
        assertThat(actualPetDto.getBirthDate().isEqual(petDto.getBirthDate()), is(true));
        assertThat(actualPetDto.getAge(), equalTo(petDto.getAge()));
        assertThat(actualPetDto.getSex(), equalTo(petDto.getSex()));
        assertThat(actualPetDto.getColor(), equalTo(petDto.getColor()));
        assertThat(actualPetDto.getSign(), equalTo(petDto.getSign()));
        assertThat(actualPetDto.getIsExhibition(), equalTo(petDto.getIsExhibition()));
        assertThat(actualPetDto.getVetVisitReason(), equalTo(petDto.getVetVisitReason()));
        assertThat(actualPetDto.getVaccine(), equalTo(petDto.getVaccine()));
        assertThat(actualPetDto.getParasites(), equalTo(petDto.getParasites()));
        assertThat(actualPetDto.getFleaMite(), equalTo(petDto.getFleaMite()));
        assertThat(actualPetDto.getSurgery(), equalTo(petDto.getSurgery()));
        assertThat(actualPetDto.getPastDisease(), equalTo(petDto.getPastDisease()));
        assertThat(actualPetDto.getHealthCharacteristic(), equalTo(petDto.getHealthCharacteristic()));
        assertThat(actualPetDto.getUrineAnalysis(), equalTo(petDto.getUrineAnalysis()));
        assertThat(actualPetDto.getIsAllergy(), equalTo(petDto.getIsAllergy()));
        assertThat(actualPetDto.getAllergyType(), equalTo(petDto.getAllergyType()));
        assertThat(actualPetDto.getIsChronicDisease(), equalTo(petDto.getIsChronicDisease()));
        assertThat(actualPetDto.getChronicDiseaseType(), equalTo(petDto.getChronicDiseaseType()));
        assertThat(actualPetDto.getHeatDate(), equalTo(petDto.getHeatDate()));
        assertThat(actualPetDto.getVetData(), equalTo(petDto.getVetData()));
        assertThat(actualPetDto.getStayWithoutMaster(), equalTo(petDto.getStayWithoutMaster()));
        assertThat(actualPetDto.getStayAlone(), equalTo(petDto.getStayAlone()));
        assertThat(actualPetDto.getSpecialCare(), equalTo(petDto.getSpecialCare()));
        assertThat(actualPetDto.getBarkHowl(), equalTo(petDto.getBarkHowl()));
        assertThat(actualPetDto.getFurnitureDamage(), equalTo(petDto.getFurnitureDamage()));
        assertThat(actualPetDto.getFoodFromTable(), equalTo(petDto.getFoodFromTable()));
        assertThat(actualPetDto.getDefecateAtHome(), equalTo(petDto.getDefecateAtHome()));
        assertThat(actualPetDto.getMarkAtHome(), equalTo(petDto.getMarkAtHome()));
        assertThat(actualPetDto.getNewPeople(), equalTo(petDto.getNewPeople()));
        assertThat(actualPetDto.getIsBitePeople(), equalTo(petDto.getIsBitePeople()));
        assertThat(actualPetDto.getReasonOfBite(), equalTo(petDto.getReasonOfBite()));
        assertThat(actualPetDto.getPlayWithDogs(), equalTo(petDto.getPlayWithDogs()));
        assertThat(actualPetDto.getIsDogTraining(), equalTo(petDto.getIsDogTraining()));
        assertThat(actualPetDto.getTrainingName(), equalTo(petDto.getTrainingName()));
        assertThat(actualPetDto.getLike(), equalTo(petDto.getLike()));
        assertThat(actualPetDto.getNotLike(), equalTo(petDto.getNotLike()));
        assertThat(actualPetDto.getToys(), equalTo(petDto.getToys()));
        assertThat(actualPetDto.getBadHabit(), equalTo(petDto.getBadHabit()));
        assertThat(actualPetDto.getWalking(), equalTo(petDto.getWalking()));
        assertThat(actualPetDto.getMorningWalking(), equalTo(petDto.getMorningWalking()));
        assertThat(actualPetDto.getDayWalking(), equalTo(petDto.getDayWalking()));
        assertThat(actualPetDto.getEveningWalking(), equalTo(petDto.getEveningWalking()));
        assertThat(actualPetDto.getFeedingQuantity(), equalTo(petDto.getFeedingQuantity()));
        assertThat(actualPetDto.getFeedType(), equalTo(petDto.getFeedType()));
        assertThat(actualPetDto.getFeedName(), equalTo(petDto.getFeedName()));
        assertThat(actualPetDto.getFeedComposition(), equalTo(petDto.getFeedComposition()));
        assertThat(actualPetDto.getFeedingRate(), equalTo(petDto.getFeedingRate()));
        assertThat(actualPetDto.getFeedingPractice(), equalTo(petDto.getFeedingPractice()));
        assertThat(actualPetDto.getTreat(), equalTo(petDto.getTreat()));
        assertThat(actualPetDto.getIsMedicine(), equalTo(petDto.getIsMedicine()));
        assertThat(actualPetDto.getMedicineRegimen(), equalTo(petDto.getMedicineRegimen()));
        assertThat(actualPetDto.getAdditionalData(), equalTo(petDto.getAdditionalData()));
    }

    @Test
    void getPetById_whenGetPetByBossAndPetNotFound_thenNotFoundExceptionThrown() {
        String error = String.format("Pet with id = %d not found", pet.getId());
        when(entityService.getUserIfExists(requesterBoss.getId())).thenReturn(requesterBoss);
        when(entityService.getPetIfExists(any())).thenThrow(new NotFoundException(error));

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> petService.getPetById(requesterBoss.getId(), 0L)
        );

        assertEquals(error, exception.getMessage());
    }

    @Test
    void getPetById_whenGetPetByAdminAndPetNotFound_thenNotFoundExceptionThrown() {
        String error = String.format("Pet with id = %d not found", pet.getId());
        when(entityService.getUserIfExists(requesterAdmin.getId())).thenReturn(requesterAdmin);
        when(entityService.getPetIfExists(any())).thenThrow(new NotFoundException(error));

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> petService.getPetById(requesterAdmin.getId(), 0L)
        );

        assertEquals(error, exception.getMessage());
    }

    @Test
    void getPetById_whenGetPetByUserAndPetNotFound_thenNotFoundExceptionThrown() {
        String error = String.format("Pet with id = %d not found", pet.getId());
        when(entityService.getUserIfExists(requesterUser.getId())).thenReturn(requesterUser);
        when(entityService.getPetIfExists(any())).thenThrow(new NotFoundException(error));

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> petService.getPetById(requesterUser.getId(), 0L)
        );

        assertEquals(error, exception.getMessage());
    }

    @Test
    void getPetById_whenUserNotFound_thenNotFoundExceptionThrown() {
        long userNotFoundId = 0L;
        String error = String.format("User with id = %d not found", userNotFoundId);
        when(entityService.getUserIfExists(userNotFoundId)).thenThrow(new NotFoundException(error));

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> petService.getPetById(userNotFoundId, pet.getId())
        );

        assertEquals(error, exception.getMessage());
        verify(mockPetRepository, times(0)).save(any());
    }

    @Test
    void updatePet_whenUpdatePetByBoss_thenReturnUpdatePetDto() {
        when(entityService.getUserIfExists(requesterBoss.getId())).thenReturn(requesterBoss);
        when(entityService.getPetIfExists(any())).thenReturn(pet);
        when(mockPetRepository.save(any())).thenReturn(updatePet);
        when(mockPetMapper.toPetDto(updatePet)).thenReturn(updatedPetDto);
        when(mockPetMapper.toPet(updatePetDto)).thenReturn(updatePet);

        PetDto actualPetDto = petService.updatePet(requesterBoss.getId(), pet.getId(), updatePetDto);

        assertNotNull(actualPetDto);
        assertThat(actualPetDto.getId(), equalTo(pet.getId()));
        assertThat(actualPetDto.getType(), equalTo(updatePet.getType()));
        assertThat(actualPetDto.getName(), equalTo(updatePet.getName()));
        assertThat(actualPetDto.getBreed(), equalTo(updatePet.getBreed()));
        assertThat(actualPetDto.getBirthDate().isEqual(updatePet.getBirthDate()), is(true));
        assertThat(actualPetDto.getAge(), equalTo(petDto.getAge()));
        assertThat(actualPetDto.getSex(), equalTo(updatePet.getSex()));
        assertThat(actualPetDto.getColor(), equalTo(updatePet.getColor()));
        assertThat(actualPetDto.getSign(), equalTo(updatePet.getSign()));
        assertThat(actualPetDto.getIsExhibition(), equalTo(updatePet.getIsExhibition()));
        assertThat(actualPetDto.getVetVisitReason(), equalTo(updatePet.getVetVisitReason()));
        assertThat(actualPetDto.getVaccine(), equalTo(updatePet.getVaccine()));
        assertThat(actualPetDto.getParasites(), equalTo(updatePet.getParasites()));
        assertThat(actualPetDto.getFleaMite(), equalTo(updatePet.getFleaMite()));
        assertThat(actualPetDto.getSurgery(), equalTo(updatePet.getSurgery()));
        assertThat(actualPetDto.getPastDisease(), equalTo(updatePet.getPastDisease()));
        assertThat(actualPetDto.getHealthCharacteristic(), equalTo(updatePet.getHealthCharacteristic()));
        assertThat(actualPetDto.getUrineAnalysis(), equalTo(updatePet.getUrineAnalysis()));
        assertThat(actualPetDto.getIsAllergy(), equalTo(updatePet.getIsAllergy()));
        assertThat(actualPetDto.getAllergyType(), equalTo(updatePet.getAllergyType()));
        assertThat(actualPetDto.getIsChronicDisease(), equalTo(updatePet.getIsChronicDisease()));
        assertThat(actualPetDto.getChronicDiseaseType(), equalTo(updatePet.getChronicDiseaseType()));
        assertThat(actualPetDto.getHeatDate(), equalTo(updatePet.getHeatDate()));
        assertThat(actualPetDto.getVetData(), equalTo(updatePet.getVetData()));
        assertThat(actualPetDto.getStayWithoutMaster(), equalTo(updatePet.getStayWithoutMaster()));
        assertThat(actualPetDto.getStayAlone(), equalTo(updatePet.getStayAlone()));
        assertThat(actualPetDto.getSpecialCare(), equalTo(updatePet.getSpecialCare()));
        assertThat(actualPetDto.getBarkHowl(), equalTo(updatePet.getBarkHowl()));
        assertThat(actualPetDto.getFurnitureDamage(), equalTo(updatePet.getFurnitureDamage()));
        assertThat(actualPetDto.getFoodFromTable(), equalTo(updatePet.getFoodFromTable()));
        assertThat(actualPetDto.getDefecateAtHome(), equalTo(updatePet.getDefecateAtHome()));
        assertThat(actualPetDto.getMarkAtHome(), equalTo(updatePet.getMarkAtHome()));
        assertThat(actualPetDto.getNewPeople(), equalTo(updatePet.getNewPeople()));
        assertThat(actualPetDto.getIsBitePeople(), equalTo(updatePet.getIsBitePeople()));
        assertThat(actualPetDto.getReasonOfBite(), equalTo(updatePet.getReasonOfBite()));
        assertThat(actualPetDto.getPlayWithDogs(), equalTo(updatePet.getPlayWithDogs()));
        assertThat(actualPetDto.getIsDogTraining(), equalTo(updatePet.getIsDogTraining()));
        assertThat(actualPetDto.getTrainingName(), equalTo(updatePet.getTrainingName()));
        assertThat(actualPetDto.getLike(), equalTo(updatePet.getLike()));
        assertThat(actualPetDto.getNotLike(), equalTo(updatePet.getNotLike()));
        assertThat(actualPetDto.getToys(), equalTo(updatePet.getToys()));
        assertThat(actualPetDto.getBadHabit(), equalTo(updatePet.getBadHabit()));
        assertThat(actualPetDto.getWalking(), equalTo(updatePet.getWalking()));
        assertThat(actualPetDto.getMorningWalking(), equalTo(updatePet.getMorningWalking()));
        assertThat(actualPetDto.getDayWalking(), equalTo(updatePet.getDayWalking()));
        assertThat(actualPetDto.getEveningWalking(), equalTo(updatePet.getEveningWalking()));
        assertThat(actualPetDto.getFeedingQuantity(), equalTo(updatePet.getFeedingQuantity()));
        assertThat(actualPetDto.getFeedType(), equalTo(updatePet.getFeedType()));
        assertThat(actualPetDto.getFeedName(), equalTo(updatePet.getFeedName()));
        assertThat(actualPetDto.getFeedComposition(), equalTo(updatePet.getFeedComposition()));
        assertThat(actualPetDto.getFeedingRate(), equalTo(updatePet.getFeedingRate()));
        assertThat(actualPetDto.getFeedingPractice(), equalTo(updatePet.getFeedingPractice()));
        assertThat(actualPetDto.getTreat(), equalTo(updatePet.getTreat()));
        assertThat(actualPetDto.getIsMedicine(), equalTo(updatePet.getIsMedicine()));
        assertThat(actualPetDto.getMedicineRegimen(), equalTo(updatePet.getMedicineRegimen()));
        assertThat(actualPetDto.getAdditionalData(), equalTo(updatePet.getAdditionalData()));
    }

    @Test
    void updatePet_whenUpdatePetPetByAdmin_thenReturnUpdatePetDto() {
        when(entityService.getUserIfExists(requesterAdmin.getId())).thenReturn(requesterAdmin);
        when(entityService.getPetIfExists(any())).thenReturn(pet);
        when(mockPetRepository.save(any())).thenReturn(updatePet);
        when(mockPetMapper.toPetDto(updatePet)).thenReturn(updatedPetDto);
        when(mockPetMapper.toPet(updatePetDto)).thenReturn(updatePet);

        PetDto actualPetDto = petService.updatePet(requesterAdmin.getId(), pet.getId(), updatePetDto);

        assertNotNull(actualPetDto);
        assertThat(actualPetDto.getId(), equalTo(pet.getId()));
        assertThat(actualPetDto.getType(), equalTo(updatePet.getType()));
        assertThat(actualPetDto.getName(), equalTo(updatePet.getName()));
        assertThat(actualPetDto.getBreed(), equalTo(updatePet.getBreed()));
        assertThat(actualPetDto.getBirthDate().isEqual(updatePet.getBirthDate()), is(true));
        assertThat(actualPetDto.getAge(), equalTo(petDto.getAge()));
        assertThat(actualPetDto.getSex(), equalTo(updatePet.getSex()));
        assertThat(actualPetDto.getColor(), equalTo(updatePet.getColor()));
        assertThat(actualPetDto.getSign(), equalTo(updatePet.getSign()));
        assertThat(actualPetDto.getIsExhibition(), equalTo(updatePet.getIsExhibition()));
        assertThat(actualPetDto.getVetVisitReason(), equalTo(updatePet.getVetVisitReason()));
        assertThat(actualPetDto.getVaccine(), equalTo(updatePet.getVaccine()));
        assertThat(actualPetDto.getParasites(), equalTo(updatePet.getParasites()));
        assertThat(actualPetDto.getFleaMite(), equalTo(updatePet.getFleaMite()));
        assertThat(actualPetDto.getSurgery(), equalTo(updatePet.getSurgery()));
        assertThat(actualPetDto.getPastDisease(), equalTo(updatePet.getPastDisease()));
        assertThat(actualPetDto.getHealthCharacteristic(), equalTo(updatePet.getHealthCharacteristic()));
        assertThat(actualPetDto.getUrineAnalysis(), equalTo(updatePet.getUrineAnalysis()));
        assertThat(actualPetDto.getIsAllergy(), equalTo(updatePet.getIsAllergy()));
        assertThat(actualPetDto.getAllergyType(), equalTo(updatePet.getAllergyType()));
        assertThat(actualPetDto.getIsChronicDisease(), equalTo(updatePet.getIsChronicDisease()));
        assertThat(actualPetDto.getChronicDiseaseType(), equalTo(updatePet.getChronicDiseaseType()));
        assertThat(actualPetDto.getHeatDate(), equalTo(updatePet.getHeatDate()));
        assertThat(actualPetDto.getVetData(), equalTo(updatePet.getVetData()));
        assertThat(actualPetDto.getStayWithoutMaster(), equalTo(updatePet.getStayWithoutMaster()));
        assertThat(actualPetDto.getStayAlone(), equalTo(updatePet.getStayAlone()));
        assertThat(actualPetDto.getSpecialCare(), equalTo(updatePet.getSpecialCare()));
        assertThat(actualPetDto.getBarkHowl(), equalTo(updatePet.getBarkHowl()));
        assertThat(actualPetDto.getFurnitureDamage(), equalTo(updatePet.getFurnitureDamage()));
        assertThat(actualPetDto.getFoodFromTable(), equalTo(updatePet.getFoodFromTable()));
        assertThat(actualPetDto.getDefecateAtHome(), equalTo(updatePet.getDefecateAtHome()));
        assertThat(actualPetDto.getMarkAtHome(), equalTo(updatePet.getMarkAtHome()));
        assertThat(actualPetDto.getNewPeople(), equalTo(updatePet.getNewPeople()));
        assertThat(actualPetDto.getIsBitePeople(), equalTo(updatePet.getIsBitePeople()));
        assertThat(actualPetDto.getReasonOfBite(), equalTo(updatePet.getReasonOfBite()));
        assertThat(actualPetDto.getPlayWithDogs(), equalTo(updatePet.getPlayWithDogs()));
        assertThat(actualPetDto.getIsDogTraining(), equalTo(updatePet.getIsDogTraining()));
        assertThat(actualPetDto.getTrainingName(), equalTo(updatePet.getTrainingName()));
        assertThat(actualPetDto.getLike(), equalTo(updatePet.getLike()));
        assertThat(actualPetDto.getNotLike(), equalTo(updatePet.getNotLike()));
        assertThat(actualPetDto.getToys(), equalTo(updatePet.getToys()));
        assertThat(actualPetDto.getBadHabit(), equalTo(updatePet.getBadHabit()));
        assertThat(actualPetDto.getWalking(), equalTo(updatePet.getWalking()));
        assertThat(actualPetDto.getMorningWalking(), equalTo(updatePet.getMorningWalking()));
        assertThat(actualPetDto.getDayWalking(), equalTo(updatePet.getDayWalking()));
        assertThat(actualPetDto.getEveningWalking(), equalTo(updatePet.getEveningWalking()));
        assertThat(actualPetDto.getFeedingQuantity(), equalTo(updatePet.getFeedingQuantity()));
        assertThat(actualPetDto.getFeedType(), equalTo(updatePet.getFeedType()));
        assertThat(actualPetDto.getFeedName(), equalTo(updatePet.getFeedName()));
        assertThat(actualPetDto.getFeedComposition(), equalTo(updatePet.getFeedComposition()));
        assertThat(actualPetDto.getFeedingRate(), equalTo(updatePet.getFeedingRate()));
        assertThat(actualPetDto.getFeedingPractice(), equalTo(updatePet.getFeedingPractice()));
        assertThat(actualPetDto.getTreat(), equalTo(updatePet.getTreat()));
        assertThat(actualPetDto.getIsMedicine(), equalTo(updatePet.getIsMedicine()));
        assertThat(actualPetDto.getMedicineRegimen(), equalTo(updatePet.getMedicineRegimen()));
        assertThat(actualPetDto.getAdditionalData(), equalTo(updatePet.getAdditionalData()));
    }

    @Test
    void updatePet_whenUpdatePetPetByBossAndPetNotFound_thenNotFoundExceptionThrown() {
        String error = String.format("Pet with id = %d not found", pet.getId());
        when(entityService.getPetIfExists(any())).thenThrow(new NotFoundException(error));

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> petService.updatePet(requesterBoss.getId(), 0L, updatePetDto)
        );

        assertEquals(error, exception.getMessage());
    }

    @Test
    void updatePet_whenUpdatePetByAdminAndPetNotFound_thenNotFoundExceptionThrown() {
        String error = String.format("Pet with id = %d not found", pet.getId());
        when(entityService.getPetIfExists(any())).thenThrow(new NotFoundException(error));

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> petService.updatePet(requesterAdmin.getId(), 0L, updatePetDto)
        );

        assertEquals(error, exception.getMessage());
    }

    @Test
    void deletePetById_whenDeletePetByBoss_thenDeletedPet() {
        when(entityService.getPetIfExists(any())).thenReturn(pet);
        when(mockPetRepository.deletePetById(any())).thenReturn(1);

        petService.deletePetById(requesterBoss.getId(), pet.getId());

        verify(mockPetRepository, times(1)).deletePetById(any());
    }

    @Test
    void deletePetById_whenDeletePetByAdmin_thenDeletedPet() {
        when(entityService.getPetIfExists(any())).thenReturn(pet);
        when(mockPetRepository.deletePetById(any())).thenReturn(1);

        petService.deletePetById(requesterAdmin.getId(), pet.getId());

        verify(mockPetRepository, times(1)).deletePetById(any());
    }

    @Test
    void deletePetById_whenDeletePetPetByBossAndPetNotFound_thenNotFoundExceptionThrown() {
        String error = String.format("pet with id=%d not found", 0);
        when(entityService.getPetIfExists(any())).thenThrow(new NotFoundException(error));

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> petService.deletePetById(requesterBoss.getId(), 0L)
        );

        assertEquals(error, exception.getMessage());
    }

    @Test
    void deletePetById_whenDeletePetByAdminAndPetNotFound_thenNotFoundExceptionThrown() {
        String error = String.format("pet with id=%d not found", 0);
        when(entityService.getPetIfExists(any())).thenThrow(new NotFoundException(error));

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> petService.deletePetById(requesterAdmin.getId(), 0L)
        );

        assertEquals(error, exception.getMessage());
    }
}