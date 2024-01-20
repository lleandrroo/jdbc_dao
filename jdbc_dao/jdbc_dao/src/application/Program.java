package application;

import java.sql.Connection;
import java.util.Date;

import db.DB;
import model.dao.DaoFactory;
import model.dao.SellerDao;
import model.entities.Department;
import model.entities.Seller;

public class Program {

	public static void main(String[] args) {
		
		SellerDao sellerDao = DaoFactory.createSellerDao(); // inversão de dependência
		System.out.println("*** TEST 1: seller findById ***");
		Seller seller = sellerDao.findById(9);		
		System.out.println(seller);
	}

}
