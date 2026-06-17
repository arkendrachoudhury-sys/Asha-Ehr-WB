export interface Patient {
    id?: number;
    identifierAadhaar: string;
    identifierAbha: string;
    name: string;
    telecomPhone: string;
    gender: string;
    birthDate: string;
    age: number;
    addressVillage: string;
    contactGuardianName: string;
    isPregnant: boolean;
    isChild: boolean;
    createdTimestamp: number;
}

export interface AncCheckup {
    id?: number;
    subjectPatientId: number;
    periodStart: string;
    lmpDate: string;
    eddDate: string;
    weightKg: number;
    systolicBp: number;
    diastolicBp: number;
    hemoglobinLevel: number;
    ifaSupplements: boolean;
    tetanusDose: string;
    highRiskFactors: string;
    clinicalNotes: string;
}

export interface NcdScreening {
    id?: number;
    subjectPatientId: number;
    effectiveDateTime: string;
    systolicBp: number;
    diastolicBp: number;
    bloodGlucose: number;
    tobaccoStatus: boolean;
    symptomsPresent: string;
    referralRequest: string;
}

export interface ConsultLog {
    id?: number;
    subjectPatientId: number;
    encounterDate: string;
    presentingSymptoms: string;
    priority: string;
    symptomDuration: number;
    bodyTemperature: number;
    fieldNotes: string;
    aiClinicalImpression: string;
    referralStatus: boolean;
}
