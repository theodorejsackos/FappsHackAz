package com.hackaz.fapps.fappshackaz;

import java.io.Serializable;

/**
 * Created by theoj on 1/15/2017.
 */

public enum UserDemographic implements Serializable {
    STUDENT_PRIMARY, STUDENT_SECONDARY, STUDENT_UNIVERSITY,
    STUDENT_GRADUATE, PROFESSIONAL_TWENTIES, PROFESSIONAL_THIRTIES,
    PROFESSIONAL_OLD;
}
