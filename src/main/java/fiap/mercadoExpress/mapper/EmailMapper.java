package fiap.mercadoExpress.mapper;

import fiap.mercadoExpress.dto.EmailDto;
import fiap.mercadoExpress.model.Compra;
import fiap.mercadoExpress.model.EmailModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EmailMapper {

    @Mapping(target = "emailId", ignore = true)
    @Mapping(target = "sendDateEmail", ignore = true)
    @Mapping(target = "statusEmail", ignore = true)
    EmailModel toModel(EmailDto emailDto);

    EmailDto toDto(EmailModel emailModel);

    @Mapping(target = "emailId", ignore = true)
    @Mapping(target = "sendDateEmail", ignore = true)
    @Mapping(target = "statusEmail", ignore = true)
    @Mapping(target = "ownerRef", constant = "Tranquilo")
    @Mapping(target = "emailFrom", source = "emailFrom")
    @Mapping(target = "emailTo", source = "compra.clienteEmail")
    @Mapping(target = "subject", expression = "java(\"Pagamento confirmado - Pedido \" + compra.getNumeroPedido())")
    @Mapping(target = "text", expression = "java(\"Olá, \" + (compra.getClienteNome() == null ? \"cliente\" : compra.getClienteNome()) + \"! O Banco Tranquilo confirmou o pagamento do pedido \" + compra.getNumeroPedido() + \" (\" + compra.getDescricao() + \") no valor de R$ \" + compra.getValorTotal() + \". Obrigado por comprar no Mercado Express!\")")
    EmailModel toConfirmacaoEmail(Compra compra, String emailFrom);
}
