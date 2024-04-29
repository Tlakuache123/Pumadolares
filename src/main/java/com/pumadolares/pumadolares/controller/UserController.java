package com.pumadolares.pumadolares.controller;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Iterator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.pumadolares.pumadolares.model.UserModel;
import com.pumadolares.pumadolares.repository.UserRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@Controller
@CrossOrigin
@RequestMapping(path = "user")
public class UserController {
  @Autowired
  public UserRepository userRepository;

  @PostMapping(path = "add")
  public ResponseEntity<Map<String, Object>> add(@RequestBody Map<String, Object> data) {
    Map<String, Object> response = new HashMap<>();

    if (!data.containsKey("name") && !data.containsKey("email")) {
      response.put("message", "Falta datos para poder agregar un usuario");
      return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    UserModel newUser = new UserModel();

    newUser.setName((String) data.get("name"));
    newUser.setEmail((String) data.get("email"));
    UserModel userSaved = userRepository.save(newUser);

    response.put("data", userSaved);
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @GetMapping(path = "get")
  public @ResponseBody Map<String, Object> getAll() {
    Iterator<UserModel> allUsers = userRepository.findAll().iterator();

    Map<String, Object> response = new HashMap<>();
    List<Map<String, Object>> userFilter = new ArrayList<>();

    while (allUsers.hasNext()) {
      Map<String, Object> resUser = new HashMap<>();
      UserModel user = allUsers.next();

      resUser.put("name", user.getName());
      resUser.put("id", user.getId());

      userFilter.add(resUser);
    }

    response.put("data", userFilter);

    return response;
  }

  @GetMapping(path = "get/{id}")
  public ResponseEntity<Map<String, Object>> getUser(@PathVariable("id") Integer id) {
    Optional<UserModel> userSearched = userRepository.findById(id);
    Map<String, Object> response = new HashMap<>();

    if (userSearched.isPresent()) {
      UserModel user = userSearched.get();
      Map<String, Object> resUser = new HashMap<>();

      resUser.put("id", user.getId());
      resUser.put("name", user.getName());
      resUser.put("email", user.getEmail());
      resUser.put("currency", user.getCurrency());

      response.put("data", resUser);
      return new ResponseEntity<>(response, HttpStatus.OK);
    }

    response.put("message", "Usuario no encontrado");
    return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);

  }

  @PutMapping(path = "money/{id}")
  public ResponseEntity<Map<String, Object>> modifyMoney(@PathVariable("id") Integer id,
      @RequestBody Map<String, Object> data) {
    Optional<UserModel> userSearched = userRepository.findById(id);
    Map<String, Object> response = new HashMap<>();

    // Check if user exists
    if (!userSearched.isPresent()) {
      response.put("message", "Usuario no encontrado");
      return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    // Check if data has money
    if (!data.containsKey("money")) {
      response.put("message", "Valor no encontrado en la peticion 'money'");
      return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    UserModel user = userSearched.get();
    double dMoney = 0.0;

    if (data.get("money") instanceof Integer) {
      dMoney = (double) ((Integer) data.get("money"));
    } else if (data.get("money") instanceof Double) {
      dMoney = (double) data.get("money");
    } else {
      dMoney = Double.valueOf((String) data.get("money"));
    }

    user.setCurrency(dMoney);
    UserModel newUserData = userRepository.save(user);

    response.put("data", newUserData);

    return new ResponseEntity<>(response, HttpStatus.OK);

  }

  @DeleteMapping(path = "delete/{id}")
  public @ResponseBody String deleteUser(@PathVariable("id") int id) {
    userRepository.deleteById(id);
    return "Usuario eliminado";
  }

}