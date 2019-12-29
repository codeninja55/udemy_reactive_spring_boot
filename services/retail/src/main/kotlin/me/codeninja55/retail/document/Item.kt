package me.codeninja55.retail.document

import lombok.AllArgsConstructor
import lombok.Data
import lombok.Generated
import lombok.NoArgsConstructor
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import reactor.core.publisher.Flux

@Document
@Data
@AllArgsConstructor
@NoArgsConstructor
data class Item(@Id var id: String?,
                var description: String,
                var price: Double)
