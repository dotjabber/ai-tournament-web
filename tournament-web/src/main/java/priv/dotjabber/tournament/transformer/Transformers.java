package priv.dotjabber.tournament.transformer;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Maciej Kowalski (dotjabber@gmail.com)
 */
public final class Transformers {

    public static <ENT, DTO> DTO toDTO(ENT input, ObjectTransformer<DTO, ENT> transformer) {
        return transformer.toDTO(input);
    }

    public static <ENT, DTO> ENT toEntity(DTO input, ObjectTransformer<DTO, ENT> transformer) {
        return transformer.toEntity(input);
    }

    public static <ENT, DTO> List<ENT> toEntities(List<DTO> input, ObjectTransformer<DTO, ENT> transformer) {
        return input.stream()
                .map(transformer::toEntity)
                .collect(Collectors.toList());
    }

    public static <DTO, ENT> List<DTO> toDTOs(List<ENT> input, ObjectTransformer<DTO, ENT> transformer) {
        return input.stream()
                .map(transformer::toDTO)
                .collect(Collectors.toList());
    }
}