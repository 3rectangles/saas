/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.common.utilities;

import com.barraiser.common.graphql.types.Interviewer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.Map;

@RunWith(MockitoJUnitRunner.class)
public class ObjectFieldsFilterTest {
	@Data
	private static class TestPojo {
		private String a;
		private Integer b;
		private TestPojo2 pojo2;
		private List<TestPojo2> testPojo2s;
		private Map<String, TestPojo2> pojo2Map;
	}

	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	private static class TestPojo2 {
		private String a;
		private Integer b;
	}

	@Test
	public void shouldSetTheUnauthorizedFieldsNull() throws IllegalAccessException {
		ObjectFieldsFilter<TestPojo> filedFilter = new ObjectFieldsFilter<>();
		TestPojo p = new TestPojo();
		p.setA("val1");
		p.setB(20);
		p.setTestPojo2s(List.of(new TestPojo2("a", 2), new TestPojo2("b", 3)));
		filedFilter.filter(p, List.of("b"));

		Assert.assertNull(p.getA());
		Assert.assertNull(p.getTestPojo2s());
		Assert.assertNull(p.getPojo2());
		Assert.assertNotNull(p.getB());
	}

	@Test
	public void shouldReturnTheSameObjectIfAllFieldsAreSpecified() throws IllegalAccessException {
		ObjectFieldsFilter<TestPojo> filedFilter = new ObjectFieldsFilter<>();
		TestPojo p = new TestPojo();
		p.setA("val1");
		p.setB(20);
		p.setPojo2(new TestPojo2());
		p.setTestPojo2s(List.of(new TestPojo2("a", 2), new TestPojo2("b", 3)));

		filedFilter.filter(p, List.of("a", "b", "pojo2"));

		Assert.assertNotNull(p.getA());
		Assert.assertNotNull(p.getB());
		Assert.assertNotNull(p.getPojo2());
		Assert.assertNull(p.getTestPojo2s());
	}

	@Test
	public void shouldReturnNullIfNoFieldIsSpecified() throws IllegalAccessException {
		ObjectFieldsFilter<TestPojo> filedFilter = new ObjectFieldsFilter<>();
		TestPojo p = new TestPojo();
		p.setA("val1");
		p.setB(20);

		filedFilter.filter(p, List.of("a", "b", "pojo2"));

		Assert.assertNull(p.getPojo2());
	}

	@Test
	public void shouldReturnNonRequiredFields() throws IllegalAccessException {
		Interviewer interviewer = Interviewer.builder()
				.id("userDetailsDAO.getId()")
				.initials("userDetailsDAO.getInitials()")
				.almaMater("userDetailsDAO.getAlmaMater()")
				.designation("userDetailsDAO.getDesignation()")
				.workExperienceInMonths(123)
				.bankAccount("expertDAO.getBankAccount()")
				.pan("expertDAO.getPan()")
				.offerLetter("expertDAO.getOfferLetter()")
				.expertDomains(List.of("asdsd"))
				.peerDomains(List.of("asdsds"))
				.totalInterviewsCompleted(10L)
				.build();

		final List<String> NON_PI_FIELDS = List.of(
				"roles",
				"role",
				"almaMater",
				"currentCompanyName",
				"workExperienceInMonths",
				"lastCompanies",
				"category");
		ObjectFieldsFilter<Interviewer> filedFilter = new ObjectFieldsFilter<>();

		filedFilter.filter(interviewer, NON_PI_FIELDS);

		System.out.println(interviewer);

	}

	@Test
	public void shouldBeAbletoProcessAList() throws IllegalAccessException {
		Interviewer interviewer1 = Interviewer.builder()
				.id("userDetailsDAO.getId()")
				.initials("userDetailsDAO.getInitials()")
				.almaMater("userDetailsDAO.getAlmaMater()")
				.designation("userDetailsDAO.getDesignation()")
				.workExperienceInMonths(123)
				.bankAccount("expertDAO.getBankAccount()")
				.pan("expertDAO.getPan()")
				.offerLetter("expertDAO.getOfferLetter()")
				.expertDomains(List.of("asdsd"))
				.peerDomains(List.of("asdsds"))
				.totalInterviewsCompleted(10L)
				.build();
		Interviewer interviewer2 = Interviewer.builder()
				.id("userDetailsDAO.getId()")
				.initials("userDetailsDAO.getInitials()")
				.almaMater("userDetailsDAO.getAlmaMater()")
				.designation("userDetailsDAO.getDesignation()")
				.workExperienceInMonths(1234)
				.bankAccount("expertDAO.getBankAccount()")
				.pan("expertDAO.getPan()")
				.offerLetter("expertDAO.getOfferLetter()")
				.expertDomains(List.of("asdsd"))
				.peerDomains(List.of("asdsds"))
				.totalInterviewsCompleted(10L)
				.build();

		final List<String> NON_PI_FIELDS = List.of(
				"roles",
				"role",
				"almaMater",
				"currentCompanyName",
				"workExperienceInMonths",
				"lastCompanies",
				"category");
		ObjectFieldsFilter<List<Interviewer>> filedFilter = new ObjectFieldsFilter<>();

		filedFilter.filter(List.of(interviewer1, interviewer2), NON_PI_FIELDS);

		System.out.println(interviewer1);
		System.out.println(interviewer2);

	}
}
