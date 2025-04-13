package entities;

import java.sql.SQLException;
import java.util.List;

public interface CrudProduit <Prod> {
    public void ajouter(Prod p);
    public void modifier(Prod p);
    public void supprimer(int id) throws SQLException;
    public List<Produit> Show();

}
