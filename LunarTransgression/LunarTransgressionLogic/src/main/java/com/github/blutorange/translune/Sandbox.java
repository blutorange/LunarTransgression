package com.github.blutorange.translune;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import com.github.blutorange.translune.db.Item;
import com.github.blutorange.translune.db.Item_;
import com.github.blutorange.translune.socket.ELunarMessageType;
import com.github.blutorange.translune.socket.ELunarStatusCode;
import com.github.blutorange.translune.socket.LunarMessage;
import com.jsoniter.JsonIterator;
import com.jsoniter.output.JsonStream;

public class Sandbox {
	public static void main(final String[] args) {
		// testing...
		new JsoniterConfig().setup();
		final LunarMessage msg = new LunarMessage(2, ELunarMessageType.AUTHORIZE, ELunarStatusCode.OK, "test");
		final String json = JsonStream.serialize(msg);
		System.out.println(json);
		final LunarMessage de = JsonIterator.deserialize(json, LunarMessage.class);
		System.out.println(de);
		System.out.println(de.getId());
		System.out.println(de.getType());
		System.out.println(de.getPayload());
		System.exit(0);

		final EntityManagerFactory emf = Persistence.createEntityManagerFactory("hibernate");
		doIt(emf);
		doIt(emf);
		doIt(emf);
	}

	public static void doIt(final EntityManagerFactory emf) {
		final EntityManager em = emf.createEntityManager();
		try {
			final CriteriaBuilder cb = em.getCriteriaBuilder();
			final CriteriaQuery<Item> q = cb.createQuery(Item.class);
			final Root<Item> r = q.from(Item.class);
			q.where(cb.equal(r.get(Item_.name), "test")).select(r);
			final TypedQuery<Item> typedQuery = em.createQuery(q);
			for (final Object o : typedQuery.getResultList()) {
				System.out.println(o);
			}
			System.out.println("");
			System.out.println("");
			System.out.println("");
			System.out.println("");
			System.out.println("");
		}
		finally {
			em.close();
		}
	}
}