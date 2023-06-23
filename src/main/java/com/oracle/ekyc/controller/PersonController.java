package com.oracle.ekyc.controller;

import com.google.gson.Gson;
import com.oracle.ekyc.controller.oci.UploadObjectStorage;
import com.oracle.ekyc.exception.ResourceNotFoundException;
import com.oracle.ekyc.model.Person;
import com.oracle.ekyc.repository.PersonRepository;
import com.oracle.ekyc.repository.SchemeRepository;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.yaml.snakeyaml.tokens.Token.ID;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.io.IOException;

/**
 * Created by shashiramachandra on 20/04/23.
 */
@RestController
@RequestMapping("/api")
public class PersonController {

    @Autowired
    PersonRepository personRepository;
    // SchemeRepository schemeRepository;

    @CrossOrigin(origins = "*", methods = { RequestMethod.POST })
    @PostMapping(value = "/fileupload")
    public String singleFileUpload(@RequestParam("file") MultipartFile file,
            RedirectAttributes redirectAttributes) throws Exception {

        System.out.println("Upload Documents controller");
        String uploadmsg = "error while uploading";
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Please select a file to upload");
            return "redirect:uploadStatus";
        }
        try {
            UploadObjectStorage uobjstorage = new UploadObjectStorage();

            uploadmsg = uobjstorage.upload(file);
            redirectAttributes.addFlashAttribute("message",
                    "You successfully uploaded '" + file.getOriginalFilename() + "'");
            uploadmsg = "{\"imgURL\" : \"" + file.getOriginalFilename() + "\", \"imgtoken\" : \"ekyc-bucket\"}";

        } catch (IOException e) {
            e.printStackTrace();
            uploadmsg = "{\"imgURL\" : \"\", \"imgtoken\" : \"\"}";
        }
        return uploadmsg;
    }

    @GetMapping("/uploadStatus")
    public String uploadStatus() {
        return "uploadStatus";
    }

    @CrossOrigin(origins = "*", methods = { RequestMethod.POST })
    @PostMapping(value = "/findperson")
    public ResponseEntity<String> findPersonByID(@RequestBody Map<String, Object> payload)
            throws Exception {
        Person person = this.fetchPersonByID(payload);
        person.setMsg("");

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("ekyc-app",
                "findperson");
        if (person.getFname() != null) {
            return ResponseEntity.status(HttpStatus.OK)
                    .headers(responseHeaders)
                    .body(new Gson().toJson(person));
        } else {
            return ResponseEntity.status(400)
                    .headers(responseHeaders)
                    .body(new Gson().toJson(person));
        }
    }

    @CrossOrigin(origins = "*", methods = { RequestMethod.POST })
    @PostMapping(value = "/initperson")
    public Person initPersonByID(@RequestBody Map<String, Object> payload)
            throws Exception {
        Person person = this.fetchPersonByID(payload);
        JSONObject obj = new JSONObject(payload);
        try {

            // if aadhaarID, secID, createdAt, updatedAt is Null (record doesnt exitsts)
            // insert the record into Table without phone, imgurl, imgtoken
            // This block is only called when the record is not present

            if (person.getAadhaarID() == null && person.getSecID() == null && person.getCreatedAt() == null
                    && person.getUpdatedAt() == null) {
                System.out.println("insert the record into Table without phone, imgurl, imgtoken");

                Boolean aadhaar = obj.getBoolean("aadhaar");
                String aadhaarID = obj.getString("aadhaarID");
                String secIDType = obj.getString("secIDType");
                String secID = obj.getString("secID");
                Integer age = obj.getInt("age");
                String fname = obj.getString("fname");
                String lname = obj.getString("lname");

                person.setUidai(false);
                person.setAadhaar(aadhaar);
                person.setAadhaarID(aadhaarID);
                person.setSecIDType(secIDType);
                person.setSecID(secID);
                person.setAge(age);
                person.setFname(fname);
                person.setLname(lname);
                person.setGr(false);
                person.setSchemes("[]");

            }

            if (!person.isUidai()) {
                // Call UIDAI for face authentication
                // Call UIDAI for phone authentication
                System.out.println("Calling UIDAI for validation");
                // if Valid
                String respuidai = "valid";
                if (respuidai == "valid") {

                    System.out.println("After receiving valid UIDAI response update the record");
                    String phone = obj.getString("phone");
                    String imgtoken = obj.getString("imgtoken");
                    String imgURL = obj.getString("imgURL");

                    person.setUidai(true);
                    person.setObjStored(true);
                    person.setInitvalid(true);
                    person.setPhone(phone);
                    person.setImgURL(imgURL);
                    person.setImgtoken(imgtoken);
                    person.setGr(true);
                    person.setMsg("apply_for_scheme");
                    return personRepository.save(person);
                } else {
                    // We will delete the record from GR

                    System.out.println("After receiving invalid UIDAI response delete the record");
                    personRepository.delete(person);
                    person.setUidai(false);
                    person.setMsg("Not a valid aadhaar no.");
                    return person;
                }

            } else {

                // Call python AI face recognition api with imgurl and imgtoken and construct
                // the full url
                // Update the imgurl,imgtoken and gr

                String imgURL = obj.getString("imgURL");
                String imgtoken = obj.getString("imgtoken");

                System.out.println(imgURL);
                System.out.println(imgtoken);
                System.out.print("Update the imgurl,imgtoken and gr--1");
                String fmresponse = this.localFaceMatch(imgURL, imgtoken);

                JSONObject fmobj = new JSONObject(fmresponse);
                System.out.println(fmobj.getString("status"));
                if (fmobj.getString("status").equals("match")) {
                    System.out.println("face matched");
                    System.out.print("Update the imgurl,imgtoken and gr--2");
                    System.out.println(fmresponse);
                    person.setImgURL(imgURL);
                    person.setImgtoken(imgtoken);

                    person.setObjStored(true);
                    person.setGr(true);

                    person.setMsg("apply_for_scheme");
                    return personRepository.save(person);
                } else {
                    System.out.println("face not matching");
                   
                    return person;
                }

            }

        } catch (Exception e) {
            // e.printStackTrace();
            return person;
        }

    }

    @CrossOrigin(origins = "*", methods = { RequestMethod.POST })
    @PostMapping(value = "/applyscheme")
    public Person applyScheme(@RequestBody Map<String, Object> payload)
            throws Exception {
        Person person = this.fetchPersonByID(payload);
        JSONObject obj = new JSONObject(payload);
        try {

            System.out.println(person.isUidai());
            System.out.println(person.isGr());
            System.out.println(person.isObjStored());

            // checking if the record exists before apply for schemes

            if (!person.isUidai() || !person.isGr() || !person.isObjStored()) {
                System.out.println("record not found");
                person.setUidai(false);
                person.setMsg("Aadhar doesnt exists");
                return person;

            } else {

                System.out.println("updating the scheme for the aadhaar");
                String aadhaarID = obj.getString("aadhaarID");
                String scheme = obj.getString("scheme");

                JSONArray jsonArrayScheme = new JSONArray(person.getSchemes());

                jsonArrayScheme.put(scheme);

                person.setSchemes(jsonArrayScheme.toString());
                person.setMsg("Applied to scheme successfully: " + scheme);
                return personRepository.save(person);

            }

        } catch (Exception e) {
            // e.printStackTrace();
            return person;
        }

    }

    @CrossOrigin(origins = "*", methods = { RequestMethod.GET })
    @GetMapping("/persons")
    public List<Person> getAllPersons() {
        return personRepository.findAll();
    }

    @PostMapping("/persons")
    public Person createPerson(@Valid @RequestBody Person person) {
        return personRepository.save(person);
    }

    @GetMapping("/persons/{id}")
    public Person getPersonById(@PathVariable(value = "id") Long personId) {
        return personRepository.findById(personId)
                .orElseThrow(() -> new ResourceNotFoundException("Person", "id", personId));
    }

    @CrossOrigin(origins = "*", methods = { RequestMethod.GET })
    @GetMapping("/persons/phone/{phone}")
    public Person getPersonByPhone(@PathVariable(value = "phone") String personPhone) {
        return personRepository.findByPhone(personPhone)
                .orElseThrow(() -> new ResourceNotFoundException("Person", "phone", personPhone));
    }

    @CrossOrigin(origins = "*", methods = { RequestMethod.GET })
    @GetMapping("/persons/aadhaar/{aadhaarID}")
    public Person getPersonByAadhaarID(@PathVariable(value = "aadhaarID") String personAadhaarID) {
        return personRepository.findByAadhaarID(personAadhaarID)
                .orElseThrow(() -> new ResourceNotFoundException("Person", "aadhaarID", personAadhaarID));
    }

    @CrossOrigin(origins = "*", methods = { RequestMethod.PUT })
    @PutMapping("/persons/{id}")
    public Person updatePerson(@PathVariable(value = "id") Long personId,
            @Valid @RequestBody Person personDetails) {

        Person person = personRepository.findById(personId)
                .orElseThrow(() -> new ResourceNotFoundException("Person", "id", personId));

        person.setInitvalid(personDetails.isInitvalid());
        person.setObjStored(personDetails.isObjStored());
        person.setUidai(personDetails.isUidai());
        person.setImgtoken(personDetails.getImgtoken());
        person.setImgURL(personDetails.getImgURL());
        person.setAadhaar(personDetails.isAadhaar());
        person.setAadhaarID(personDetails.getAadhaarID());
        person.setSecIDType(personDetails.getSecIDType());
        person.setSecID(personDetails.getSecID());
        person.setAge(personDetails.getAge());
        person.setFname(personDetails.getFname());
        person.setLname(personDetails.getLname());
        person.setGr(personDetails.isGr());
        person.setMsg(personDetails.getMsg());
        person.setSchemes(personDetails.getSchemes());

        Person updatedPerson = personRepository.save(person);
        return updatedPerson;
    }

    // @DeleteMapping("/persons/{id}")
    // public ResponseEntity<?> deletePerson(@PathVariable(value = "id") Long
    // personId) {
    // Person person = personRepository.findById(personId)
    // .orElseThrow(() -> new ResourceNotFoundException("Person", "id", personId));

    // personRepository.delete(person);

    // return ResponseEntity.ok().build();
    // }

    public Person fetchPersonByID(Map<String, Object> payload) {
        Person person = new Person();
        try {
            JSONObject obj = new JSONObject(payload);
            String personAadhaarID = obj.getString("aadhaarID");
            String secID = obj.getString("secID");

            if (obj.getBoolean("aadhaar") && !obj.getString("aadhaarID").isEmpty()) {
                person = personRepository.findByAadhaarID(personAadhaarID)
                        .orElseThrow(() -> new ResourceNotFoundException("Person", "aadhaarID", personAadhaarID));
            } else if (!obj.getString("secID").isEmpty() && !obj.getString("secIDtype").isEmpty()) {
                person = personRepository.findBySecID(secID)
                        .orElseThrow(() -> new ResourceNotFoundException("Person", "secID", secID));
            }

            return person;
        } catch (Exception e) {
            e.printStackTrace();
            return person;
        }
    }

    public String localFaceMatch(String imgurl, String imgtoken) {
        // Create a RestTemplate object
        String faceresponse = null;
        try {

            RestTemplate restTemplate = new RestTemplate();
            System.out.println("-----------------1");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBasicAuth("user_imgmatch", "pwd_imgmatch");

            imgurl = "KFDS432LJ-324324324324.jpg";
            String requestBody = "{\"imgtoken\":\"" + imgtoken + "\",\"imgurl\":\"" + imgurl + "\"}";

            HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

            System.out.println("-----------------2");

            // Make the API call
            String apiUrl = "http://152.67.30.46:8080/checkimg";
            System.out.println("-----------------3");
            faceresponse = restTemplate.postForObject(apiUrl, request, String.class);
            System.out.println("-----------------4");
        } catch (Exception e) {
            System.out.print(e);
        }
        return faceresponse;

    }

}
