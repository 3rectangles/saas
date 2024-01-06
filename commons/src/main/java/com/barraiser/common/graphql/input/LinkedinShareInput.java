package com.barraiser.common.graphql.input;
import com.barraiser.common.graphql.types.Question;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class LinkedinShareInput {
    @NotNull(message = "code not present")
    private String code;

    @NotNull(message = "certificateId not present")
    private String certificateId;


}
