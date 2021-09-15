package com.cool.nfckiosk.data.store;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class Store {

    private String id;
    private String adminId;
    private String adminNickname;
    private String title;
    private List<Table> tables;
    private long created;


    public Store() {
    }

    public Store(String adminId, String adminNickname, String title, List<Table> tables) {
        this.adminId = adminId;
        this.adminNickname = adminNickname;
        this.title = title;
        this.tables = tables;
        this.created = System.currentTimeMillis();
        this.id = adminId + "#" + created;
    }

    public String getId() {
        return id;
    }

    public String getAdminId() {
        return adminId;
    }

    public String getAdminNickname() {
        return adminNickname;
    }

    public String getTitle() {
        return title;
    }

    public List<Table> getTables() {
        return tables;
    }

    public long getCreated() {
        return created;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }

    public void setAdminNickname(String adminNickname) {
        this.adminNickname = adminNickname;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setTables(List<Table> tables) {
        this.tables = tables;
    }

    public void setCreated(long created) {
        this.created = created;
    }


    public static class Table implements Serializable {

        private int number;
        private boolean active;

        public Table() {
        }

        public Table(int number, boolean active) {
            this.number = number;
            this.active = active;
        }

        public void setNumber(int number) {
            this.number = number;
        }

        public void setActive(boolean active) {
            this.active = active;
        }

        public int getNumber() {
            return number;
        }

        public boolean isActive() {
            return active;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Table table = (Table) o;
            return number == table.number && active == table.active;
        }

        @Override
        public int hashCode() {
            return Objects.hash(number, active);
        }
    }

}
