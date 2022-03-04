package com.example.demo.src.address;

import com.example.demo.src.address.model.Address;
import com.example.demo.src.address.model.GetAddressRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class AddressDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void createUserAddress(Address address) {
        String createAddressQuery = "insert into UserAddress (userId, address, details, addressType, nickName) VALUES (?,?,?,?,?)";
        Object[] createAddressParams = new Object[]{address.getUserId(), address.getAddress(), address.getDetails(), address.getAddressType(), address.getNickName()};
        this.jdbcTemplate.update(createAddressQuery, createAddressParams);
    }

    public int checkUserStatusByUserId(int userId) {
        String checkUserStatusByUserIdQuery = "select exists(select * from User where id = ? )";
        int checkUserStatusByUserIdParams = userId;
        return this.jdbcTemplate.queryForObject(checkUserStatusByUserIdQuery, int.class, checkUserStatusByUserIdParams);
    }


    public void modifyAddressTypeHouse(int userId){
        String modifyAddressTypeHouseQuery = "update UserAddress set addressType ='E' where userId = ? and addressType = 'H'";
        int modifyAddressTypeHouseParams = userId;
        this.jdbcTemplate.update(modifyAddressTypeHouseQuery, modifyAddressTypeHouseParams);
    }

    public void modifyAddressTypeCompany(int userId){
        String modifyAddressTypeCompanyQuery = "update UserAddress set addressType ='E where userId = ? and addressType = 'C'";
        int modifyAddressTypeCompanyParams = userId;
        this.jdbcTemplate.update(modifyAddressTypeCompanyQuery, modifyAddressTypeCompanyParams);
    }

    public List<GetAddressRes> getAddresses(int userId) {
        String getAddressesQuery = "select id as addressId, case when addressType = 'H' then '집' else\n"+
        "case when addressType = 'C' then '회사' else nickName end\n"+
        "end as title, concat(address, ' ', details) as detailAddress\n"+
        "from UserAddress where userId = ? order by field(addressType, 'C', 'H') desc, id desc";
        int getAddressesParams = userId;
        return this.jdbcTemplate.query(getAddressesQuery,
                (rs, rowNum) -> new GetAddressRes(
                        rs.getInt("addressId"),
                        rs.getString("title"),
                        rs.getString("detailAddress")),
                getAddressesParams);
    }

    public void modifyAddressActiveY(int userId, int addressId) {
        String modifyAddressActiveYQuery = "update UserAddress set userId = ? and id = ?";
        Object[] modifyAddressActiveYParams = new Object[]{userId, addressId};
        this.jdbcTemplate.update(modifyAddressActiveYQuery, modifyAddressActiveYParams);
    }

    public int checkAddressId(int userId, int addressId) {
        String checkAddressIdQuery = "select(exists(select * from UserAddress where and userId = ? and id = ?))";
        Object[] checkAddressIdParams = new Object[]{userId, addressId};
        return this.jdbcTemplate.queryForObject(checkAddressIdQuery, int.class, checkAddressIdParams);
    }
}