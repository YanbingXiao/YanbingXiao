--Yanbing
CREATE TABLE Caregivers (
    Username varchar(255),
    Salt BINARY(16),
    Hash BINARY(16),
    PRIMARY KEY (Username)
);

CREATE TABLE Vaccines(
    Name varchar(255),
    Doses int,
    PRIMARY KEY (Name)
);

CREATE TABLE Patient(

username VARCHAR(255),
PRIMARY KEY(username),

Salt BINARY(16),
Hash BINARY(16)
);


CREATE TABLE appoinment(
ID INT identity(1,1) PRIMARY KEY,
ti date,
PUsername VARCHAR(255)  REFERENCES Patient(username),
  Username varchar(255) REFERENCES Caregivers(Username),
  Name VARCHAR(255) REFERENCES Vaccines(Name));






CREATE TABLE Availabilities (
    Time date,
    Username varchar(255) REFERENCES Caregivers,
    PRIMARY KEY (Time, Username)
);


