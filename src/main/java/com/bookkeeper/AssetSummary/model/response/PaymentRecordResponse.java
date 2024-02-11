package com.bookkeeper.AssetSummary.model.response;

import com.bookkeeper.AssetSummary.model.dto.PaymentDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = true)
@RequiredArgsConstructor
public class PaymentRecordResponse extends BaseResponse {
    List<PaymentDTO> queryPaymentRecord;
}
