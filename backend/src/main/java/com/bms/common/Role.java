package com.bms.common;

/**
 * 角色枚举
 */
public enum Role {
    ADMIN(Permission.USER_CREATE, Permission.USER_READ, Permission.USER_UPDATE, Permission.USER_DELETE,
          Permission.USER_READ_OWN, Permission.USER_UPDATE_OWN,
          Permission.BOOK_CREATE, Permission.BOOK_READ, Permission.BOOK_UPDATE, Permission.BOOK_DELETE,
          Permission.BORROW_CREATE, Permission.BORROW_READ, Permission.BORROW_UPDATE, Permission.BORROW_DELETE,
          Permission.BORROW_READ_OWN, Permission.BORROW_CREATE_OWN,
          Permission.ROLE_CREATE, Permission.ROLE_READ, Permission.ROLE_UPDATE, Permission.ROLE_DELETE,
          Permission.PERMISSION_CREATE, Permission.PERMISSION_READ, Permission.PERMISSION_UPDATE, Permission.PERMISSION_DELETE,
          Permission.SYSTEM_CONFIG),

    LIBRARIAN(Permission.USER_READ, Permission.USER_READ_OWN, Permission.USER_UPDATE_OWN,
              Permission.BOOK_CREATE, Permission.BOOK_READ, Permission.BOOK_UPDATE, Permission.BOOK_DELETE,
              Permission.BORROW_CREATE, Permission.BORROW_READ, Permission.BORROW_UPDATE,
              Permission.BORROW_READ_OWN, Permission.BORROW_CREATE_OWN),

    READER(Permission.USER_READ_OWN, Permission.USER_UPDATE_OWN,
           Permission.BOOK_READ,
           Permission.BORROW_READ_OWN, Permission.BORROW_CREATE_OWN);

    private final Permission[] permissions;

    Role(Permission... permissions) {
        this.permissions = permissions;
    }

    public Permission[] getPermissions() {
        return permissions;
    }

    public boolean hasPermission(Permission permission) {
        for (Permission p : permissions) {
            if (p == permission) {
                return true;
            }
        }
        return false;
    }
}
