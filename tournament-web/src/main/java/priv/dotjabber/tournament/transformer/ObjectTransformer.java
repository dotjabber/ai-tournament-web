package priv.dotjabber.tournament.transformer;

/**
 * @author Maciej Kowalski (dotjabber@gmail.com)
 */
public interface ObjectTransformer<DTO, ENT> {
    ENT toEntity(DTO input);
    DTO toDTO(ENT input);
}
