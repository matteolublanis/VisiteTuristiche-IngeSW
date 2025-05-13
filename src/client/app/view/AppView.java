package client.app.view;

import java.util.List;

import dto.DTO;
import dto.DataDisponibilitaDTO;
import dto.LuogoDTO;
import dto.PrenotazioneDTO;
import dto.TipoVisitaDTO;
import dto.UserDTO;
import dto.VisitaDTO;

public interface AppView {
	public void viewPrimoAccesso();
	public void viewListDTO(List<DTO> list);
	public void viewListDataDisponibilitaDTO(List<DataDisponibilitaDTO> list);
	public void viewListLuogoDTO(List<LuogoDTO> list);
	public void viewListPrenotazioneDTO(List<PrenotazioneDTO> list);
	public void viewListTipoVisitaDTO(List<TipoVisitaDTO> list);
	public void viewListUserDTO(List<UserDTO> list);
	public void viewListVisitaDTO(List<VisitaDTO> list);
}
