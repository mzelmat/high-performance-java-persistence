package com.vladmihalcea.book.hpjp.hibernate.collection;

import com.vladmihalcea.book.hpjp.util.AbstractTest;
import org.junit.Test;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * <code>UnidirectionalBag</code> - Unidirectional Bag Test
 *
 * @author Vlad Mihalcea
 */
public class UnidirectionalMapTest extends AbstractTest {

    @Override
    protected Class<?>[] entities() {
        return new Class<?>[]{
                Person.class,
                Phone.class,
        };
    }

    @Test
    public void testLifecycle() {
        doInJPA(entityManager -> {
            Person person = new Person(1L);
            LocalDateTime now = LocalDateTime.now();
            person.addPhone(new Phone(PhoneType.LAND_LINE, "028-234-9876", Timestamp.valueOf(now)));
            person.addPhone(new Phone(PhoneType.MOBILE, "072-122-9876", Timestamp.valueOf(now.minusDays(1))));
            entityManager.persist(person);
        });
        doInJPA(entityManager -> {
            Person person = entityManager.find(Person.class, 1L);
            Map<Date, Phone> phones = person.getPhoneRegister();
            assertEquals(2, phones.size());
        });
    }

    @Entity(name = "Person")
    public static class Person {

        @Id
        private Long id;

        public Person() {
        }

        public Person(Long id) {
            this.id = id;
        }

        @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
        @JoinTable(
            name="phone_register",
            joinColumns = @JoinColumn(name = "phone_id"),
            inverseJoinColumns = @JoinColumn(name = "person_id"))
        @MapKey(name="since")
        @MapKeyTemporal(TemporalType.TIMESTAMP)
        private Map<Date, Phone> phoneRegister = new HashMap<>();

        public Map<Date, Phone> getPhoneRegister() {
            return phoneRegister;
        }

        public void addPhone(Phone phone) {
            phoneRegister.put(phone.getSince(), phone);
        }
    }

    public enum PhoneType {
        LAND_LINE,
        MOBILE
    }

    @Entity(name = "Phone")
    public static class Phone {

        @Id
        @GeneratedValue
        private Long id;

        private PhoneType type;

        private String number;

        private Date since;

        public Phone() {
        }

        public Phone(PhoneType type, String number, Date since) {
            this.type = type;
            this.number = number;
            this.since = since;
        }

        public PhoneType getType() {
            return type;
        }

        public String getNumber() {
            return number;
        }

        public Date getSince() {
            return since;
        }
    }
}
