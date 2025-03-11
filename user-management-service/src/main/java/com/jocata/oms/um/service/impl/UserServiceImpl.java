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
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;

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

    @Override
    public Map<String, Object> createUsersFromFile(MultipartFile file) {
        Map<String, Object> response = new HashMap<>();
        List<String> errors = new ArrayList<>();
        List<UserForm> createdUsers = new ArrayList<>();

        try (InputStream inputStream = file.getInputStream()) {
            Workbook workbook = WorkbookFactory.create(inputStream);
            Sheet sheet = workbook.getSheetAt(0);


            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;
                try {
                    User user = new User();
                    user.setFullName(row.getCell(0).getStringCellValue());
                    user.setEmail(row.getCell(1).getStringCellValue());
                    user.setPasswordHash(row.getCell(2).getStringCellValue());
                    user.setPhone(row.getCell(3).getStringCellValue());
                    user.setProfilePicture(
                            !row.getCell(4).getStringCellValue().isBlank() ? row.getCell(4).getStringCellValue() : "NOT_FOUND"
                    );
                    user.setOtpSecret(
                            !row.getCell(5).getStringCellValue().isBlank() ? row.getCell(5).getStringCellValue() : "NOT_SET"
                    );


                    String addressData = row.getCell(6).getStringCellValue();
                    Set<AddressForm> addressesForm = parseAddresses(addressData);
                    Set<Address> addresses=getAddresses(addressesForm);
                    user.setAddresses(addresses);

                    String rolesData = row.getCell(7).getStringCellValue();
                    String permissionData = row.getCell(8).getStringCellValue();

                    Set<RoleForm> roleForms = parseRoles(rolesData,permissionData);
                    Set<Role> roles=new HashSet<>();
                    for (RoleForm roleForm : roleForms) {
                        Role role = getRole(roleForm);
                        roleDao.createRole(role);
                        roles.add(role);
                    }
                    user.setRoles(roles);

                    User savedUser = userMgntDao.createUser(user);

                    UserForm savedUserForm = getUserForm(savedUser);

                    createdUsers.add(savedUserForm);
                } catch (Exception e) {
                    errors.add("Error in row " + row.getRowNum() + ": " + e.getMessage());
                }
            }

            response.put("createdUsers", createdUsers);
            response.put("errors", errors);
        } catch (IOException e) {
            response.put("error", "Failed to read the Excel file: " + e.getMessage());
        }

        return response;
    }

    private Set<RoleForm> parseRoles(String rolesData, String permissionData) {
        Set<RoleForm> roles = new HashSet<>();
        String[] roleParts = rolesData.split(";");
        String[] permissionParts = permissionData.split(";");

        for (int i = 0; i < roleParts.length; i++) {
            RoleForm role = new RoleForm();
            role.setRoleName(roleParts[i].trim());

            Set<PermissionForm> permissions = new HashSet<>();
            if (i < permissionParts.length) {
                String[] permissionsForRole = permissionParts[i].split(",");
                for (String perm : permissionsForRole) {
                    PermissionForm permission = new PermissionForm();
                    permission.setPermissionName(perm.trim());
                    permissions.add(permission);
                }
            }

            role.setPermissions(permissions);
            roles.add(role);
        }

        return roles;
    }


    private Set<AddressForm> parseAddresses(String data) {
        Set<AddressForm> addresses = new HashSet<>();
        String[] addressParts = data.split(";");

        for (String part : addressParts) {
            String[] details = part.split(",");
            AddressForm address = new AddressForm();
            address.setAddress(details[0]);
            address.setCity(details[1]);
            address.setState(details[2]);
            address.setCountry(details[3]);
            address.setZipCode(details[4]);
            addresses.add(address);
        }
        return addresses;
    }

    @Override
    public UserForm getUserById(Integer userId) {
        User user = userMgntDao.finUserById(userId);
        return user.getDeletedAt()!= null ? null : getUserForm(user);
    }

    @Override
    public UserForm getUserByEmail(String email) {
        User byEmail = userMgntDao.findByEmail(email);
        return byEmail.getDeletedAt()!= null ? null : getUserForm(byEmail);
    }

    @Override
    public UserForm getUserByEmail(String email, String password) {
        User userByEmail = userMgntDao.findUserByEmailAndPass(email, password);
        return userByEmail.getDeletedAt()!= null ? null : getUserForm(userByEmail);
    }

    @Override
    public List<UserForm> getAllUsers() {
        List<UserForm> userForms = new ArrayList<>();
        for (User user : userMgntDao.getAllUsers()) {
            if(user.getDeletedAt() == null){
                UserForm userForm = getUserForm(user);
                userForms.add(userForm);
            }
        }
        return userForms;
    }

    @Override
    public UserForm updateUser(UserForm user) {
        User findedUser = userMgntDao.finUserById(Integer.valueOf(user.getUserId()));
        if(findedUser != null) {
            findedUser.setUpdatedAt(Timestamp.from(Instant.now()));
            findedUser.setFullName(user.getFullName());
            findedUser.setEmail(user.getEmail());
            findedUser.setPhone(user.getPhone());
            findedUser.setPasswordHash(user.getPasswordHash());
            findedUser.setProfilePicture( !user.getProfilePicture().isBlank() ? user.getProfilePicture() : "NOT_FOUND");
            findedUser.setOtpSecret( !user.getOtpSecret().isBlank() ? user.getOtpSecret() : "NOT_SET");

            Set<AddressForm> addresses = user.getAddresses();

            if(addresses != null) {
                Set<Address> addressSet = new HashSet<>();
                for (AddressForm addressForm : addresses) {
                    if (addressForm.getAddressId() != null) {
                        Address addressById = addressDao.getAddressById(Integer.valueOf(addressForm.getAddressId()));
                        addressById.setCity(addressForm.getCity());
                        addressById.setCountry(addressForm.getCountry());
                        addressById.setState(addressForm.getState());
                        addressById.setZipCode(addressForm.getZipCode());
                        addressById.setAddress(addressForm.getAddress());
                        Address updateAddress = addressDao.updateAddress(addressById);
                        addressSet.add(updateAddress);
                    } else {
                        Address singleAddress = getSingleAddress(addressForm);
                        singleAddress.setUser(findedUser);
                        Address savedAddress = addressDao.createAddress(singleAddress);
                        addressSet.add(savedAddress);
                    }
                }
                findedUser.setAddresses(addressSet);
            }

            Set<RoleForm> roles = user.getRoles();
            if (roles!=null) {
                Set<Role> roleSet = new HashSet<>();
                for (RoleForm roleForm : roles) {
                    if (roleForm.getRoleId() != null) {
                        Role roleById = roleDao.getRoleById(Integer.valueOf(roleForm.getRoleId()));
                        roleById.setRoleName(roleForm.getRoleName());

                        Set<PermissionForm> permissionForms = roleForm.getPermissions();
                        Set<Permission> permissions = new HashSet<>();
                        for (PermissionForm permissionForm : permissionForms) {
                            if (permissionForm.getPermissionId() != null) {
                                Permission permissionById = permissionDao.getPermissionById(Integer.valueOf(permissionForm.getPermissionId()));
                                permissionById.setPermissionName(permissionForm.getPermissionName());
                                permissionDao.updatePermission(permissionById);
                                permissions.add(permissionById);
                            } else {
                                Permission permission = new Permission();
                                permission.setPermissionName(permissionForm.getPermissionName());
                                Permission savedPermission = permissionDao.createPermission(permission);
                                permissions.add(savedPermission);
                            }
                        }
                        roleById.setPermissions(permissions);
                        Role role = roleDao.updateRole(roleById);
                        roleSet.add(role);
                    } else {
                        Role role = getRole(roleForm);
                        Role savedRole = roleDao.createRole(role);
                        roleSet.add(savedRole);
                    }
                }
                findedUser.setRoles(roleSet);
            }
            User updatedUser = userMgntDao.updateUser(findedUser);
            return getUserForm(updatedUser);
        }
        return null;
    }

    @Override
    public String deleteUser(Integer userId, Boolean isHardDelete) {
        User user = userMgntDao.finUserById(userId);
        if(isHardDelete){
            userMgntDao.permanentDeleteUser(user);
            return "User deleted successfully via hard Delete.";
        }
        user.setDeletedAt(Timestamp.from(Instant.now()));
        User deletedUser = userMgntDao.softDeleteUser(user);
        return deletedUser.getDeletedAt() != null ? "User deleted successfully via soft delete." : "User not deleted..";
    }

    private User getUser(UserForm user) {

        User newUser = new User();
        newUser.setFullName(user.getFullName());
        newUser.setEmail(user.getEmail());
        newUser.setPhone(user.getPhone());
//        newUser.setPasswordHash(new BCryptPasswordEncoder().encode(user.getPasswordHash()));
        newUser.setPasswordHash(user.getPasswordHash());
        newUser.setProfilePicture( !user.getProfilePicture().isBlank() ? user.getProfilePicture() : "NOT_FOUND");
        newUser.setOtpSecret( !user.getOtpSecret().isBlank() ? user.getOtpSecret() : "NOT_SET");

        Set<Address> addresses = getAddresses(user.getAddresses());
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

    private Set<Address> getAddresses(Set<AddressForm> addresseForms) {
        Set<Address> addresses = new HashSet<>();
        for (AddressForm addressForm : addresseForms) {
            Address address = getSingleAddress(addressForm);

            addressDao.createAddress(address);

            addresses.add(address);
        }
        return addresses;
    }

    private Address getSingleAddress(AddressForm addressForm) {
        Address address = new Address();
        address.setCity(addressForm.getCity());
        address.setCountry(addressForm.getCountry());
        address.setState(addressForm.getState());
        address.setZipCode(addressForm.getZipCode());
        address.setAddress(addressForm.getAddress());
        return address;
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
        userForm.setPasswordHash(user.getPasswordHash());
        userForm.setProfilePicture(user.getProfilePicture());
        userForm.setOtpSecret(user.getOtpSecret());

        Set<AddressForm> addressForms = getAddressForms(user);
        userForm.setAddresses(addressForms);

        Set<RoleForm> roleForms = getRoleForms(user);
        userForm.setRoles(roleForms);

        return userForm;
    }

    private static Set<AddressForm> getAddressForms(User user) {
        Set<AddressForm> addressForms = new HashSet<>();
        for (Address address : user.getAddresses()) {
            AddressForm addressForm = new AddressForm();
            addressForm.setAddressId(String.valueOf(address.getAddressId()));
            addressForm.setCity(address.getCity());
            addressForm.setCountry(address.getCountry());
            addressForm.setState(address.getState());
            addressForm.setZipCode(address.getZipCode());
            addressForm.setAddress(address.getAddress());
            addressForms.add(addressForm);
        }
        return addressForms;
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
