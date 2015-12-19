package com.vladmihalcea.book.hpjp.hibernate.collection;

import com.vladmihalcea.book.hpjp.util.AbstractTest;
import org.junit.Test;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * <code>UnidirectionalBag</code> - Unidirectional Bag Test
 *
 * @author Vlad Mihalcea
 */
public class UnidirectionalBagTest extends AbstractTest {

    @Override
    protected Class<?>[] entities() {
        return new Class<?>[] {
            Person.class,
            Phone.class,
        };
    }

    @Test
    public void testLifecycle() {
        doInJPA(entityManager -> {
            Person person = new Person(1L);
            person.getPhones().add(new Phone(1L, "landline", "028-234-9876"));
            person.getPhones().add(new Phone(2L, "mobile", "072-122-9876"));
            entityManager.persist(person);
        });
        doInJPA(entityManager -> {
            Person person = entityManager.find(Person.class, 1L);
            person.getPhones().remove(0);
        });
    }

    @Entity(name = "Person")
    public static class Person  {

        @Id
        private Long id;

        public Person() {}

        public Person(Long id) {
            this.id = id;
        }

        @OneToMany(cascade = CascadeType.ALL)
        private List<Phone> phones = new ArrayList<>();

        public List<Phone> getPhones() {
            return phones;
        }
    }

    @Entity(name = "Phone")
    public static class Phone  {

        @Id
        private Long id;

        private String type;

        private String number;

        public Phone() {
        }

        public Phone(Long id, String type, String number) {
            this.id = id;
            this.type = type;
            this.number = number;
        }

        public Long getId() {
            return id;
        }

        public String getType() {
            return type;
        }

        public String getNumber() {
            return number;
        }
    }
}
