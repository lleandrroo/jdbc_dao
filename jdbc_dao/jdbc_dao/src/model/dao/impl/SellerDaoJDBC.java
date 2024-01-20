package model.dao.impl;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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
		try {
			PreparedStatement ps = connection.prepareStatement(
					"insert into seller "
					+ " (name,email,birthdate,baseSalary,department_id)"
					+ " values(?,?,?,?,?)",
					Statement.RETURN_GENERATED_KEYS);
			ps.setString(1, dao.getName());
			ps.setString(2, dao.getEmail());
			ps.setDate(3, new Date(dao.getBirthdate().getTime()));
			ps.setDouble(4, dao.getBaseSalary());
			ps.setInt(5, dao.getDepartment().getId());
			
			int rowsAffected = ps.executeUpdate();
			
			if(rowsAffected > 0) {
				ResultSet rs = ps.getGeneratedKeys();
				if(rs.next()) {
					int id = rs.getInt(1);
					dao.setId(id);
				}
				DB.closeResultSet();
			}else {
				throw new DbException("Unexpect error! No rows affected!");
			}		
		}catch(SQLException e) {
			throw new DbException(e.getMessage());
		}finally {
			DB.coloseStatement();
		}
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
		try {
			PreparedStatement ps = connection
					.prepareStatement("select seller.*,department.name as dpName" 
							+ " from seller inner join department"
							+ " on seller.department_id = department.id" 
							+ " order by name");
					ResultSet rs = ps.executeQuery();
					
					List<Seller> list = new ArrayList<>();
					Map<Integer, Department> map = new TreeMap<>();
					while(rs.next()) {
						Department dpDTO = map.get(rs.getInt("department_id")); 
						if(dpDTO == null) {
							dpDTO =	instantiateDepartment(rs);
							map.put(rs.getInt("department_id"), dpDTO);
						}
						Seller selDTO = instantiateSeller(rs, dpDTO);
						list.add(selDTO);						
					}
					return list;
		} catch (SQLException e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	@Override
	public List<Seller> findByDepartment(Department department) {
		try {
			PreparedStatement ps = connection
					.prepareStatement("select seller.*,department.name as dpName" + " from seller inner join department"
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
