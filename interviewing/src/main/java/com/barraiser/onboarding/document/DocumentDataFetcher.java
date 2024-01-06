package com.barraiser.onboarding.document;

import com.barraiser.onboarding.graphql.MultiParentTypeDataFetcher;
import com.barraiser.onboarding.jobRoleManagement.SkillInterviewingConfiguration.dal.EntityToDocumentMappingDAO;
import com.barraiser.common.graphql.types.Document;
import com.barraiser.common.graphql.types.SkillInterviewingConfiguration.SkillInterviewingConfiguration;
import com.barraiser.onboarding.jobRoleManagement.SkillInterviewingConfiguration.repository.EntityToDocumentMappingRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import graphql.execution.DataFetcherResult;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLObjectType;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class DocumentDataFetcher implements MultiParentTypeDataFetcher {

    private final ObjectMapper objectMapper;

    private final EntityToDocumentMappingRepository entityToDocumentMappingRepository;
    private final DocumentRepository documentRepository;

    private static final String DOCUMENT_CONTEXT_SAMPLE_QUESTION = "SAMPLE_QUESTION";
    private static final String ENTITY_TYPE_SKILL_CONFIGURATION = "SKILL_INTERVIEWING_CONFIGURATION";

    @Override
    public List<List<String>> typeNameMap() {
        return List.of(
            List.of("SkillInterviewingConfiguration", "sampleQuestionDocuments")
        );
    }

    @Override
    public Object get(final DataFetchingEnvironment environment) throws Exception {

        final GraphQLObjectType type = (GraphQLObjectType) environment.getParentType();

        if (type.getName().equals("SkillInterviewingConfiguration")) {

            if (environment.getFieldDefinition().getName().equals("sampleQuestionDocuments")) {
                final SkillInterviewingConfiguration skillInterviewingConfiguration = environment.getSource();
                final List<Document> sampleQuestionDocuments = this.getDocumentsForEntity(skillInterviewingConfiguration);

                return DataFetcherResult.newResult()
                    .data(sampleQuestionDocuments)
                    .build();
            }
        } else {
            throw new IllegalArgumentException("Bad parent type while accessing skill type, please fix your query");
        }

        return DataFetcherResult.newResult().build();
    }

    private List<Document> getDocumentsForEntity(final SkillInterviewingConfiguration skillInterviewingConfiguration) {
        List<EntityToDocumentMappingDAO> entityToDocumentMappingDAOS = this.entityToDocumentMappingRepository.findByEntityIdAndEntityVersionAndEntityTypeAndContext(skillInterviewingConfiguration.getId(), skillInterviewingConfiguration.getVersion(), ENTITY_TYPE_SKILL_CONFIGURATION, DOCUMENT_CONTEXT_SAMPLE_QUESTION);

        List<String> documentIds = entityToDocumentMappingDAOS.stream()
            .map(ed -> ed.getDocumentId())
            .collect(Collectors.toList());

        return this.documentRepository.findAllById(documentIds).stream()
            .map(d -> Document.builder().id(d.getDocumentId()).fileName(d.getFileName()).url(d.getFileUrl()).build())
            .collect(Collectors.toList());
    }
}
