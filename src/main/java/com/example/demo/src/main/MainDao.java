package com.example.demo.src.main;

import com.example.demo.src.main.model.*;
import com.example.demo.src.user.model.PostLoginReq;
import com.example.demo.src.user.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class MainDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }


    public String getMainAddress(int userId) {
        String getMainAddressQuery = "select (case when addressType = 'C' then '회사' \n" +
                " when addressType = 'H' then '집' \n" +
                "when addressType='E' and nickName is not null then nickName\n"+
                "else address end) as title from UserAddress where userId = ? and represent=1";
        int getMainAddressParams = userId;
        return this.jdbcTemplate.queryForObject(getMainAddressQuery, String.class, getMainAddressParams);
    }


    public List<GetStoreCategoryRes> getStoreCategoryRes() {
        String getStoreCategoryResQuery = "select id, name, iconUrl\n" +
                "from Category";
        return this.jdbcTemplate.query(getStoreCategoryResQuery,
                (rs, rowNum) -> new GetStoreCategoryRes(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("iconUrl"))
        );
    }
}
