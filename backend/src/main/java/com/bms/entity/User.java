/**
 * 用户实体类
 */
package com.bms.entity;

import com.bms.common.validation.ValidationGroup;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import java.io.Serializable;

/**
 * 用户实体类
 */
@Data
public class User implements Serializable {

    private Integer id;

    @NotBlank(message = "用户名不能为空", groups = {ValidationGroup.Add.class, ValidationGroup.Update.class})
    @Size(min = 3, max = 20, message = "用户名长度必须在 3-20 之间", groups = {ValidationGroup.Add.class, ValidationGroup.Update.class})
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "用户名只能包含字母、数字和下划线", groups = {ValidationGroup.Add.class, ValidationGroup.Update.class})
    private String username;

    @NotBlank(message = "密码不能为空", groups = {ValidationGroup.Add.class})
    @Size(min = 6, max = 50, message = "密码长度必须在 6-50 之间", groups = {ValidationGroup.Add.class})
    private String password;

    @Pattern(regexp = "^(ADMIN|LIBRARIAN|READER)$", message = "角色只能是 ADMIN、LIBRARIAN 或 READER",
             groups = {ValidationGroup.Add.class})
    private String role;

    private Boolean enabled = true;
}
