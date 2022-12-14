package pl.lodz.p.edu.data.model.users;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotEmpty;
import pl.lodz.p.edu.data.model.DTO.users.EmployeeDTO;

@Entity
@DiscriminatorValue("employee")
public class Employee extends User {

    @Column(name = "desk")
    @NotEmpty
    private String desk;

    public Employee() {}

    public Employee(String login, String desk) {
        super(login);
        this.desk = desk;
    }

    public Employee(EmployeeDTO employeeDTO) {
        this.merge(employeeDTO);
    }



    public boolean verify() {
        return super.verify() && !desk.isEmpty();
    }

    public void merge(EmployeeDTO employeeDTO) {
        this.setLogin(employeeDTO.getLogin());
        this.desk = employeeDTO.getDesk();
    }

    public String getDesk() {
        return desk;
    }

    public void setDesk(String desk) {
        this.desk = desk;
    }

}
