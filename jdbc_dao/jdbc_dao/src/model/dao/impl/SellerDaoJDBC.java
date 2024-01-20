package model.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import db.DB;
import db.DbException;
import model.dao.SellerDao;
import model.entities.Department;
import model.entities.Seller;

public class SellerDaoJDBC implements SellerDao {
	private Connection connection;

	public SellerDaoJDBC(Connection conn) {
		this.connection = conn;
	}

	@Override
	public void insert(Seller dao) {

	}

	@Override
	public void update(Seller dao) {
	}

	@Override
	public void deleteById(Integer id) {
	}

	@Override
	public Seller findById(Integer id) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = connection.prepareStatement(
					"select seller.*,department.name as dpName " + "from seller inner join department "
							+ "on seller.department_id = department.id " + "where seller.id = ?");
			ps.setInt(1, id);
			rs = ps.executeQuery();
			if (rs.next()) {
				Department dpDTO = instantiateDepartment(rs);
				Seller sellDTO = instantiateSeller(rs, dpDTO);
				return sellDTO;
			}
			return null;
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.coloseStatement();
			DB.closeResultSet();
		}

	}

	private Seller instantiateSeller(ResultSet rs, Department dpDTO) throws SQLException {
		Seller sellDTO = new Seller();
		sellDTO.setId(rs.getInt("id"));
		sellDTO.setName(rs.getString("name"));
		sellDTO.setEmail(rs.getString("email"));
		sellDTO.setBirthdate(rs.getDate("birthdate"));
		sellDTO.setBaseSalary(rs.getDouble("baseSalary"));
		sellDTO.setDepartment(dpDTO);
		return sellDTO;
	}

	private Department instantiateDepartment(ResultSet rs) throws SQLException {
		Department dpDTO = new Department();
		dpDTO.setId(rs.getInt("id"));
		dpDTO.setName(rs.getString("dpName"));
		return dpDTO;
	}

	@Override
	public List<Seller> findAll() {
		return null;
	}

	@Override
	public List<Seller> findByDepartment(Department department) {
		try {
			PreparedStatement ps = connection.prepareStatement(
					"select seller.*,department.name as dpName" + " from seller inner join department"
							+ " on seller.department_id = department.id" + " where department_id = ? ");
			ps.setInt(1, department.getId());
			ResultSet rs = ps.executeQuery();

			List<Seller> ls = new ArrayList<>();
			Map<Integer, Department> map = new HashMap<>();

			while (rs.next()) {
				Department dep = map.get(rs.getInt("department_id"));
				if (dep == null) {
					dep = instantiateDepartment(rs);
					map.put(rs.getInt("department_id"), dep);
				}
				Seller sel = instantiateSeller(rs, dep);
				ls.add(sel);
			}
			return ls;
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
	}

}
