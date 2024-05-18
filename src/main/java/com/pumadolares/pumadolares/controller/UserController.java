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
import com.pumadolares.pumadolares.service.EmailService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.mail.MessagingException;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@CrossOrigin
@RequestMapping(path = "/api/user")
public class UserController {
  @Autowired
  public UserRepository userRepository;

  @Autowired
  private EmailService emailService;

  @Operation(summary = "Inserta un nuevo usuario", description = "Agrega un nuevo usuario a la base de datos")
  @PostMapping(path = "/")
  @ResponseBody
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

  @Operation(summary = "Obtener todos los usuarios", description = "Obtienes la informacion resumida de todos los usuarios guardados")
  @GetMapping(path = "/")
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

  @Operation(summary = "Obten un usuario por su id", description = "Obten toda la informacion de un usario especificado por su id")
  @GetMapping(path = "/{id}")
  @ResponseBody
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

  @Operation(summary = "Modifica el dinero de un usario", description = "Suma o resta dinero a la cuenta de un usuario")
  @PutMapping(path = "money/{id}")
  @ResponseBody
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

  @Operation(summary = "Elimina un usuario por su id", description = "Elimina a un usuario de la base de datos por medio de su id")
  @DeleteMapping(path = "/{id}")
  public @ResponseBody String deleteUser(@PathVariable("id") int id) {
    userRepository.deleteById(id);
    return "Usuario eliminado";
  }

  @PostMapping("notify")
  public Map<String, String> sendEmail(@RequestBody Map<String, String> body) {
    Map<String, String> response = new HashMap<>();
    try {
      if (!body.containsKey("to") || !body.containsKey("subject") || !body.containsKey("message")) {
        response.put("message", "datos faltantes para enviar un correo");
        return response;
      }
      String to = body.get("to");
      String subject = body.get("subject");
      String message = body.get("message");

      Optional<UserModel> userSearched = userRepository.findById(Integer.parseInt(to));

      if (!userSearched.isPresent()) {
        response.put("message", "No se encontro un usario al que mandarle correo");
      }

      UserModel user = userSearched.get();

      emailService.sendEmail(user.getEmail(), subject, message);
      response.put("message", "correo enviado correctamente");
      return response;
    } catch (MessagingException err) {
      System.err.println(err);
      response.put("message", "error al enviar correo: " + err.getMessage());
      return response;
    }
  }

}