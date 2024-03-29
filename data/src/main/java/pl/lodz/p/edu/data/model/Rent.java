package pl.lodz.p.edu.data.model;

import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

import pl.lodz.p.edu.data.model.DTO.MvcRentDTO;
import pl.lodz.p.edu.data.model.DTO.RentDTO;
import pl.lodz.p.edu.data.model.users.Client;

@Entity
@Table(name = "rent")
@Transactional
@Access(AccessType.FIELD)
public class Rent extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(initialValue = 0, name = "rent_sequence_generator")
    @Column(name = "rent_id")
    private long id;

    @NotNull
    @JoinColumn(name = "equipment_id")
    @ManyToOne(fetch = FetchType.EAGER,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Equipment equipment;

    @NotNull
    @JoinColumn(name = "client_id")
    @ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Client client;

    @NotNull
    @Column(name = "begin_time")
    private LocalDateTime beginTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;


    public Rent(LocalDateTime beginTime, LocalDateTime endTime,
                Equipment equipment, Client client) {
        this.beginTime = beginTime;
        this.endTime = endTime;
        this.equipment = equipment;
        this.client = client;
    }

    public Rent() {}

    public Rent(RentDTO rentDTO, Equipment equipment, Client client) {
        this.beginTime = LocalDateTime.parse(rentDTO.getBeginTime());
        if(rentDTO.getEndTime() != null) {
            this.endTime = LocalDateTime.parse(rentDTO.getEndTime());
        } else {
            this.endTime = null;
        }
        this.equipment = equipment;
        this.client = client;
    }

    public Rent(long id, LocalDateTime beginTime, LocalDateTime endTime,
                Equipment equipment, Client client) {

        this(beginTime, endTime, equipment, client);
        this.id = id;
    }

    public boolean verify() {
        boolean check = true;
        if(endTime != null) {
            check = beginTime.isBefore(endTime);
        }
        return check && client.verify() && equipment.verify();
    }


//    public double getRentCost() {
//        long diffDays = Math.abs( ChronoUnit.DAYS.between(beginTime, endTime));
//        if (diffDays > 1) {
//            return equipment.getFirstDayCost() + equipment.getNextDaysCost() * (diffDays - 1);
//        } else {
//            return equipment.getFirstDayCost();
//        }
//    }

    public String toString() {
        final StringBuilder sb = new StringBuilder("Rent{");
        sb.append("id=").append(id);
        sb.append("Klient=").append(getClient().toString());
        sb.append("Sprzęt=").append(getEquipment().toString());
        sb.append("Czas wypożyczenia=");
        sb.append("Początek=").append(beginTime);
        sb.append(" do ");
        sb.append("Koniec=").append(endTime);
        sb.append('}');
        return sb.toString();
    }


    public void merge(RentDTO rentDTO, Equipment equipment, Client client) {
        this.beginTime = LocalDateTime.parse(rentDTO.getBeginTime());
        if(rentDTO.getEndTime() != null) {
            this.endTime = LocalDateTime.parse(rentDTO.getEndTime());
        } else {
            this.endTime = null;
        }
        this.equipment = equipment;
        this.client = client;
    }

    public void merge(MvcRentDTO mvcRentDTO) {
        this.beginTime = LocalDateTime.parse(mvcRentDTO.getBeginTime());
        this.endTime = LocalDateTime.parse(mvcRentDTO.getEndTime());
        this.client = mvcRentDTO.getClient();
        this.client.setActive(mvcRentDTO.isActive());
        this.equipment = mvcRentDTO.getEquipment();
    }

    public void merge(Rent rent) {
        this.beginTime = rent.getBeginTime();
        this.endTime = rent.getEndTime();
        this.equipment = rent.getEquipment();
        this.client = rent.getClient();
//        this.client.setActive(rent.client.isActive());
    }
    //Ja tu tak bardzo nie wiem co ty miałeś w planach robiąc to mvcRentDto

    public LocalDateTime getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(LocalDateTime beginTime) {
        this.beginTime = beginTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public Equipment getEquipment() {
        return equipment;
    }

    public void setEquipment(Equipment equipment) {
        this.equipment = equipment;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }
}
