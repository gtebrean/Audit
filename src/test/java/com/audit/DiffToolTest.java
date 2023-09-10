package com.audit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.audit.entities.Payment;
import com.audit.entities.Subscription;
import com.audit.entities.User;
import com.audit.entities.VehicleWithAdnotation;
import com.audit.entities.VehicleWithId;
import com.audit.entities.WrongVehicle;
import com.audit.exception.AuditException;
import com.audit.exception.DifferentObjectsAuditException;
import com.audit.exception.InvalidFieldsAuditException;
import com.audit.type.ChangeType;
import com.audit.type.ListUpdate;
import com.audit.type.PropertyUpdate;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DiffToolTest {

  private DiffTool diffTool;

  @BeforeAll
  void setUp() {
    diffTool = new DiffTool();
  }

  @Test
  void differentObjectsTest() {
    User user = new User();
    VehicleWithId vehicle = new VehicleWithId();
    Assertions.assertThrows(
        DifferentObjectsAuditException.class, () -> diffTool.diff(user, vehicle));
  }

  @Test
  void nullObjectsTest() throws AuditException {
    assertEquals(null, diffTool.diff(null, null));
  }

  @Test
  void propertyUpdateTest() throws AuditException {
    User previous = new User("Vlad", 30, BigDecimal.TEN, null, null);
    User current = new User("Tudor", 31, BigDecimal.ZERO, null, null);

    PropertyUpdate propertyFirstName = new PropertyUpdate("firstName", "Vlad", "Tudor");
    PropertyUpdate propertyAge = new PropertyUpdate("age", "30", "31");
    PropertyUpdate propertyWalletBalance = new PropertyUpdate("walletBalance", "10", "0");

    List<ChangeType> expectedResult =
        Arrays.asList(propertyFirstName, propertyAge, propertyWalletBalance);
    List<ChangeType> result = diffTool.diff(previous, current);
    assertEquals(expectedResult, result);
  }

  @Test
  void null_properties_updateTest() throws AuditException {
    Payment payment = new Payment(BigDecimal.TEN);
    Subscription subscription = new Subscription("ACTIVE", payment);
    User previous = new User("Vlad", 30, null, null, null);
    User current = new User("Vlad", 30, BigDecimal.TEN, subscription, null);

    PropertyUpdate propertyWalletBalance = new PropertyUpdate("walletBalance", "null", "10");
    PropertyUpdate propertyStatus = new PropertyUpdate("subscription.status", "null", "ACTIVE");
    PropertyUpdate propertyPayment = new PropertyUpdate("subscription.payment.value", "null", "10");

    List<ChangeType> expectedResult =
        Arrays.asList(propertyWalletBalance, propertyStatus, propertyPayment);
    List<ChangeType> result = diffTool.diff(previous, current);
    assertEquals(expectedResult, result);
  }

  @Test
  void nested_properties_updateTest() throws AuditException {
    Payment previousPayment = new Payment(BigDecimal.TEN);
    Payment currentPayment = new Payment(BigDecimal.valueOf(44.5));

    Subscription previousSubscription = new Subscription("ACTIVE", previousPayment);
    Subscription currentSubscription = new Subscription("EXPIRED", currentPayment);

    User previous = new User("Vlad", 31, BigDecimal.TEN, previousSubscription, null);
    User current = new User("Vlad", 31, BigDecimal.TEN, currentSubscription, null);

    PropertyUpdate expectedStatus = new PropertyUpdate("subscription.status", "ACTIVE", "EXPIRED");
    PropertyUpdate expectedPayment = new PropertyUpdate("subscription.payment.value", "10", "44.5");

    List<ChangeType> expectedResult = Arrays.asList(expectedStatus, expectedPayment);
    List<ChangeType> result = diffTool.diff(previous, current);
    assertEquals(expectedResult, result);
  }

  @Test
  void item_update_inListTest() throws AuditException {

    List<String> previousService = Arrays.asList("Change Oil", "Change Tyres");
    List<String> currentService = Arrays.asList("Change Oil", "Check Engine");

    VehicleWithId previous = new VehicleWithId();
    previous.setServices(previousService);
    VehicleWithId current = new VehicleWithId();
    current.setServices(currentService);

    ListUpdate expected =
        new ListUpdate(
            "services",
            Collections.singletonList("Check Engine"),
            Collections.singletonList("Change Tyres"));

    List<ChangeType> expectedResult = Collections.singletonList(expected);
    List<ChangeType> result = diffTool.diff(previous, current);
    assertEquals(expectedResult, result);
  }

  @Test
  void nested_item_updateInListTest() throws AuditException {

    List<String> service = Arrays.asList("Change Oil", "Change Tyres");

    VehicleWithId vehicle1 = new VehicleWithId("v_1", "Dacia", service);
    VehicleWithId vehicle2 = new VehicleWithId("v_2", "Renault", service);

    User previous = new User("Vlad", 31, BigDecimal.TEN, null, Collections.singletonList(vehicle1));
    User current = new User("Vlad", 31, BigDecimal.TEN, null, Arrays.asList(vehicle1, vehicle2));

    ListUpdate expected =
        new ListUpdate("vehicles", Collections.singletonList(vehicle2), Collections.emptyList());

    List<ChangeType> expectedResult = Collections.singletonList(expected);
    List<ChangeType> result = diffTool.diff(previous, current);
    assertEquals(expectedResult, result);
  }

  @Test
  void invalid_item_inListTest() {

    List<String> service = Arrays.asList("Change Oil", "Change Tyres");

    WrongVehicle previousVehicle = new WrongVehicle("v_1", "Dacia", service);
    WrongVehicle currentVehicle = new WrongVehicle("v_1", "Renault", service);

    User previous =
        new User("Vlad", 31, BigDecimal.TEN, null, Collections.singletonList(previousVehicle));
    User current =
        new User("Vlad", 31, BigDecimal.TEN, null, Collections.singletonList(currentVehicle));

    assertThrows(InvalidFieldsAuditException.class, () -> diffTool.diff(previous, current));
  }

  @Test
  void items_withId_updateInListTest() throws AuditException {

    List<String> service = Arrays.asList("Change Oil", "Change Tyres");

    VehicleWithId previousVehicle = new VehicleWithId("v_1", "Dacia", service);
    VehicleWithId currentVehicle = new VehicleWithId("v_1", "Renault", service);
    VehicleWithId newVehicle = new VehicleWithId("v_2", "Mercedes", service);

    User previous =
        new User("Vlad", 31, BigDecimal.TEN, null, Collections.singletonList(previousVehicle));
    User current =
        new User("Vlad", 31, BigDecimal.TEN, null, Arrays.asList(currentVehicle, newVehicle));

    PropertyUpdate expectedProp =
        new PropertyUpdate("vehicles[v_1].displayName", "Dacia", "Renault");
    ListUpdate expectedList =
        new ListUpdate("vehicles", Collections.singletonList(newVehicle), Collections.emptyList());
    List<ChangeType> expectedResult = Arrays.asList(expectedProp, expectedList);
    List<ChangeType> result = diffTool.diff(previous, current);
    assertEquals(expectedResult, result);
  }

  @Test
  void items_withId_noUpdateInListTest() throws AuditException {

    List<String> service = Arrays.asList("Change Oil", "Change Tyres");

    VehicleWithId currentVehicle = new VehicleWithId("v_1", "Renault", service);
    VehicleWithId newVehicle = new VehicleWithId("v_2", "Mercedes", service);

    User previous =
        new User("Vlad", 31, BigDecimal.TEN, null, Arrays.asList(currentVehicle, newVehicle));
    User current =
        new User("Vlad", 31, BigDecimal.TEN, null, Arrays.asList(currentVehicle, newVehicle));

    new ListUpdate("vehicles", Collections.singletonList(newVehicle), Collections.emptyList());
    List<ChangeType> result = diffTool.diff(previous, current);
    assertEquals(Collections.emptyList(), result);
  }

  @Test
  void item_withAnnotation_updateInListTest() throws AuditException {

    List<String> service = Arrays.asList("Change Oil", "Change Tyres");

    VehicleWithAdnotation previousVehicle = new VehicleWithAdnotation("v_1", "Dacia", service);
    VehicleWithAdnotation currentVehicle = new VehicleWithAdnotation("v_1", "Renault", service);

    User previous =
        new User("Vlad", 31, BigDecimal.TEN, null, Collections.singletonList(previousVehicle));
    User current =
        new User("Vlad", 31, BigDecimal.TEN, null, Collections.singletonList(currentVehicle));

    PropertyUpdate expected = new PropertyUpdate("vehicles[v_1].displayName", "Dacia", "Renault");

    List<ChangeType> expectedResult = Collections.singletonList(expected);
    List<ChangeType> result = diffTool.diff(previous, current);
    assertEquals(expectedResult, result);
  }
}
