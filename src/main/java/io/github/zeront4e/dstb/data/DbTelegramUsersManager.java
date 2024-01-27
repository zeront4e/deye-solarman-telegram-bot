/*
Copyright 2024 zeront4e (https://github.com/zeront4e)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at
   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package io.github.zeront4e.dstb.data;

import com.j256.ormlite.dao.Dao;
import io.github.zeront4e.dstb.data.dao.DbTelegramUser;

import java.sql.SQLException;
import java.util.List;

public class DbTelegramUsersManager {
    private final Dao<DbTelegramUser, Long> dbTelegramUserDao;

    public DbTelegramUsersManager(Dao<DbTelegramUser, Long> dbTelegramUserDao) {
        this.dbTelegramUserDao = dbTelegramUserDao;
    }

    public DbTelegramUser getDbTelegramUserOrNull(long userId) throws SQLException {
        List<DbTelegramUser> dbTelegramUsers = dbTelegramUserDao.queryBuilder()
                .where()
                .eq("userId", userId)
                .query();

        if(dbTelegramUsers.isEmpty())
            return null;

        return dbTelegramUsers.get(0);
    }

    public List<DbTelegramUser> getDbTelegramUsers() throws SQLException {
        return dbTelegramUserDao.queryForAll();
    }

    public void createTelegramUser(DbTelegramUser dbTelegramUser) throws SQLException {
        dbTelegramUserDao.create(dbTelegramUser);
    }

    public void updateTelegramUser(DbTelegramUser dbTelegramUser) throws SQLException {
        dbTelegramUserDao.update(dbTelegramUser);
    }

    public void deleteTelegramUser(DbTelegramUser dbTelegramUser) throws SQLException {
        deleteTelegramUser(dbTelegramUser.getId());
    }

    public void deleteTelegramUser(long userId) throws SQLException {
        dbTelegramUserDao.deleteById(userId);
    }
}