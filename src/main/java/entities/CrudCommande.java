package entities;

import java.sql.SQLException;
import java.util.List;

public interface CrudCommande <Cmd> {
    public void ajouter(Cmd c);
    public void modifier(Cmd c);
    public void supprimer(int id) throws SQLException;
    public List<Commande> Show();

}
