package application;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;

import db.DB;
import model.entities.Department;
import model.entities.Seller;

public class Program {

	public static void main(String[] args) {

		Connection conexao = DB.getConnection();
		Department dp = new Department(1, "Books");
		Seller sell = new Seller(11, "Bob", "bob@email.com", new Date(), 3000.0, dp);
		System.out.println(sell);

	}

}
