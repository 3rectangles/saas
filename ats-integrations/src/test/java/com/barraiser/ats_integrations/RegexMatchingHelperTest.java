/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.ats_integrations;

import com.barraiser.ats_integrations.calendar_interception.RegexMatchingHelper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class RegexMatchingHelperTest {
	@InjectMocks
	private RegexMatchingHelper regexMatchingHelper;

	@Test
	public void testMatchingInterviewStructureForSkillate() {

		final String text = "Game Producer: R2 :Rusheel Sandri | Gameberry Labs Privat... @ Mon Dec 19, 2022 3:30pm - 4:30pm (IST)";
		final String regex = "([A-Za-z0-9\\s]*[:]{1}[A-Za-z0-9\\s]*):.*\\|.*";
		final List<String> matchedValues = this.regexMatchingHelper.getMatchedValuesForRegex(text, regex);

		Assert.assertEquals(matchedValues.size(), 1);
		System.out.println(matchedValues.get(0));
	}

	@Test
	public void testMatchingInterviewFeedbackLinkForSkillate() {
		final String text = "<b>Feedback Link</b>&nbsp;<i>(for interviewer use only)</i><br><a href=\"https://app.skillate.com/#/interviews/566253/feedback\">Click here to submit feedback</a><br>in case of difficulties, copy and paste the link below in your browser<br><a href=\"https://app.skillate.com/#/interviews/566253/feedback\">https://app.skillate.com/#/<wbr>interviews/566253/feedback</a>";
		final String regex = ".*href=\"(https://.*/interviews/.*/feedback)\".*";
		final List<String> matchedValues = this.regexMatchingHelper.getMatchedValuesForRegex(text, regex);

		Assert.assertEquals(matchedValues.size(), 1);
		System.out.println(matchedValues.get(0));
	}

	@Test
	public void testMatchingEvaluationIdForSkillate() {

		final String text = "Game Producer: R2 :Rusheel Sandri | Gameberry Labs Privat... @ Mon Dec 19, 2022 3:30pm - 4:30pm (IST)";
		final String regex = "[^:]*:[^:]*:([A-Za-z\\s]*)\\|.*"; // had to comment because of spotless
		final List<String> matchedValues = this.regexMatchingHelper.getMatchedValuesForRegex(text, regex);

		Assert.assertEquals(matchedValues.size(), 1);
	}

	@Test
	public void testMatchInterviewFeedbackLink() {
		final String text = "<html-blob><u></u>Hi Karan,&nbsp;\\n<u></u>\\n<u></u><u></u><u></u>Please interview Mariette Maxie.<u></u>\\n<u></u>Interview type: Behavioral Phone Interview.<u></u><u></u><u></u><p>\n"
				+
				"\\nFeedback Link (for interviewer use only)\\nClick here to submit feedback\\nin case of difficulties, copy and paste the link below in your browser\\n https://app.skillate.com/#/interviews/566253/feedback\\n\\n</p>\\n\\n<u></u>\\n<u></u></html-blob>";
		final String regex = ".*(https://.*/interviews/.*/feedback).*";

		final List<String> matchedValues = this.regexMatchingHelper.getMatchedValuesForRegex(text, regex);

		Assert.assertEquals(matchedValues.size(), 1);

	}

	@Test
	public void testMatchInterviewStructureIdForGreenhouse() {
		final String text = "<html-blob><u></u>Hi Karan,&nbsp;\\n<u></u>\\n<u></u><u></u><u></u>Please interview Mariette Maxie.<u></u>\\n<u></u>Interview type: Behavioral Phone Interview.<u></u><u></u><u></u><p>\n"
				+
				"\\nFeedback Link (for interviewer use only)\\nClick here to submit feedback\\nin case of difficulties, copy and paste the link below in your browser\\n https://app4.greenhouse.io/guides/4346171004/people/11774665004/interview?application_id=12813436004\\n\\n</p>\\n\\n<u></u>\\n<u></u></html-blob>";
		final String regex = ".*[http|https]://.*\\/guides\\/([_a-zA-Z0-9]*)\\/people\\/.*\\/interview\\?application_id=.*";

		final List<String> matchedValues = this.regexMatchingHelper.getMatchedValuesForRegex(text, regex);

		Assert.assertEquals(matchedValues.size(), 1);

	}

	@Test
	public void testMatchATSEvaluationIdForGreenhouse() {
		final String text = "<html-blob><u></u>Hi Karan,&nbsp;\\n<u></u>\\n<u></u><u></u><u></u>Please interview Mariette Maxie.<u></u>\\n<u></u>Interview type: Behavioral Phone Interview.<u></u><u></u><u></u><p>\n"
				+
				"\\nFeedback Link (for interviewer use only)\\nClick here to submit feedback\\nin case of difficulties, copy and paste the link below in your browser\\n https://app4.greenhouse.io/guides/4346171004/people/11774665004/interview?application_id=12813436004\\n\\n</p>\\n\\n<u></u>\\n<u></u></html-blob>";
		final String regex = ".*[http|https]://.*\\/guides\\/.*\\/people\\/.*\\/interview\\?application_id=([_a-zA-Z0-9]*).*";

		final List<String> matchedValues = this.regexMatchingHelper.getMatchedValuesForRegex(text, regex);

		Assert.assertEquals(matchedValues.size(), 1);

	}

}
