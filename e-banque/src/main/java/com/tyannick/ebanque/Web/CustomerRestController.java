package com.tyannick.ebanque.Web;

import java.util.List;

//import org.hibernate.mapping.List;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tyannick.ebanque.Dtos.CustomerDto;
import com.tyannick.ebanque.Exception.CustomerNotFoundException;
import com.tyannick.ebanque.Service.BankAccountService;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@CrossOrigin("*")
public class CustomerRestController {
    private BankAccountService bankAccountService;

    @GetMapping("/customers")
    public List<CustomerDto> customer(){
        return bankAccountService.listCustomer();
    }
    @GetMapping("/customers/search")
    public List<CustomerDto> searchCustomer(@RequestParam(name = "keyword",defaultValue = "") String keyword){
        return bankAccountService.searchCustomers("%"+keyword+"%");
    }
    @GetMapping("/customers/{id}")
    public CustomerDto getCustomer(@PathVariable(name = "id") Long customerid) throws CustomerNotFoundException {
        return bankAccountService.getCustomer(customerid);
    }
    @PostMapping("/customers")
    public CustomerDto saveCustomer(@RequestBody CustomerDto customerDto){
        return bankAccountService.saveCustomer(customerDto);
    }

    @PutMapping("/customers/{customerId}")
    public CustomerDto UpdateCustomer(@PathVariable Long customerId,@RequestBody CustomerDto customerDto){
        customerDto.setId(customerId);
        return bankAccountService.UpdateCustomer(customerDto);
    }
    @DeleteMapping("/customer/{id}")
    public void deleteCustomer(@PathVariable(name = "id") Long customerId){
         bankAccountService.deleteCustomer(customerId);
    }
}
