package com.jocata.oms.um.service.impl;

import com.jocata.oms.data.um.dao.AddressDao;
import com.jocata.oms.data.um.dao.PermissionDao;
import com.jocata.oms.data.um.dao.RoleDao;
import com.jocata.oms.data.um.dao.UserMgntDao;
import com.jocata.oms.datamodel.um.entity.Address;
import com.jocata.oms.datamodel.um.entity.Permission;
import com.jocata.oms.datamodel.um.entity.Role;
import com.jocata.oms.datamodel.um.entity.User;
import com.jocata.oms.datamodel.um.form.AddressForm;
import com.jocata.oms.datamodel.um.form.PermissionForm;
import com.jocata.oms.datamodel.um.form.RoleForm;
import com.jocata.oms.datamodel.um.form.UserForm;
import com.jocata.oms.um.service.UserService;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService {

    private final UserMgntDao userMgntDao;
    private final AddressDao addressDao;
    private final RoleDao roleDao;
    private final PermissionDao permissionDao;

    public UserServiceImpl(UserMgntDao userMgntDao, AddressDao addressDao, RoleDao roleDao, PermissionDao permissionDao) {
        this.userMgntDao = userMgntDao;
        this.addressDao = addressDao;
        this.roleDao = roleDao;
        this.permissionDao = permissionDao;
    }

    @Override
    public UserForm registerUser(UserForm user) {

        User newUser = getUser(user);
        User savedUser = userMgntDao.createUser(newUser);
        return getUserForm(savedUser);

    }

    private User getUser(UserForm user) {

        User newUser = new User();
        newUser.setFullName(user.getFullName());
        newUser.setEmail(user.getEmail());
        newUser.setPhone(user.getPhone());
        newUser.setPasswordHash(user.getPasswordHash());
        newUser.setProfilePicture( !user.getProfilePicture().isBlank() ? user.getProfilePicture() : "NOT_FOUND");
        newUser.setOtpSecret( !user.getOtpSecret().isBlank() ? user.getOtpSecret() : "NOT_SET");

        Set<Address> addresses = getAddresses(user);
        newUser.setAddresses(addresses);

        Set<Role> roles = new HashSet<>();
        for (RoleForm roleForm : user.getRoles()) {
            Role role = getRole(roleForm);

            roleDao.createRole(role);
            roles.add(role);
        }
        newUser.setRoles(roles);
        return newUser;
    }

    private Set<Address> getAddresses(UserForm user) {
        Set<Address> addresses = new HashSet<>();
        for (AddressForm addressForm : user.getAddresses()) {
            Address address = new Address();
            address.setCity(addressForm.getCity());
            address.setCountry(addressForm.getCountry());
            address.setState(addressForm.getState());
            address.setZipCode(addressForm.getZipCode());
            address.setAddress(addressForm.getAddress());

            addressDao.createAddress(address);

            addresses.add(address);
        }
        return addresses;
    }

    private Role getRole(RoleForm roleForm) {
        Role role = new Role();
        role.setRoleName(roleForm.getRoleName());

        Set<PermissionForm> permissionForm = roleForm.getPermissions();
        Set<Permission> permissions = new HashSet<>();
        for (PermissionForm permissionForm1 : permissionForm) {
            Permission permission = new Permission();
            permission.setPermissionName(permissionForm1.getPermissionName());

            permissionDao.createPermission(permission);
            permissions.add(permission);
        }

        role.setPermissions(permissions);
        return role;
    }

    private UserForm getUserForm(User user) {
        UserForm userForm = new UserForm();
        userForm.setUserId(String.valueOf(user.getUserId()));
        userForm.setFullName(user.getFullName());
        userForm.setEmail(user.getEmail());
        userForm.setPhone(user.getPhone());
        userForm.setProfilePicture(user.getProfilePicture());
        userForm.setOtpSecret(user.getOtpSecret());

        Set<AddressForm> addressForms = new HashSet<>();
        for (Address address : user.getAddresses()) {
            AddressForm addressForm = new AddressForm();
            addressForm.setCity(address.getCity());
            addressForm.setCountry(address.getCountry());
            addressForm.setState(address.getState());
            addressForm.setZipCode(address.getZipCode());
            addressForm.setAddress(address.getAddress());
            addressForms.add(addressForm);
        }
        userForm.setAddresses(addressForms);

        Set<RoleForm> roleForms = getRoleForms(user);
        userForm.setRoles(roleForms);

        return userForm;
    }

    private static Set<RoleForm> getRoleForms(User user) {
        Set<RoleForm> roleForms = new HashSet<>();
        for (Role role : user.getRoles()) {
            RoleForm roleForm = new RoleForm();
            roleForm.setRoleId(String.valueOf(role.getRoleId()));
            roleForm.setRoleName(role.getRoleName());

            Set<PermissionForm> permissionForms = new HashSet<>();
            for (Permission permission : role.getPermissions()) {
                PermissionForm permissionForm = new PermissionForm();
                permissionForm.setPermissionId(String.valueOf(permission.getPermissionId()));
                permissionForm.setPermissionName(permission.getPermissionName());
                permissionForms.add(permissionForm);
            }
            roleForm.setPermissions(permissionForms);
            roleForms.add(roleForm);
        }
        return roleForms;
    }
}
