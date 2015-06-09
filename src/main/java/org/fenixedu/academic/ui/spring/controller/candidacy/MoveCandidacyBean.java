package org.fenixedu.academic.ui.spring.controller.candidacy;

import org.fenixedu.academic.domain.candidacyProcess.CandidacyProcess;
import org.fenixedu.academic.domain.candidacyProcess.IndividualCandidacyProcess;

public class MoveCandidacyBean {

    private IndividualCandidacyProcess candidacy;
    private CandidacyProcess candidacyProcess;

    public IndividualCandidacyProcess getCandidacy() {
        return candidacy;
    }

    public void setCandidacy(IndividualCandidacyProcess candidacy) {
        this.candidacy = candidacy;
    }

    public CandidacyProcess getCandidacyProcess() {
        return candidacyProcess;
    }

    public void setCandidacyProcess(CandidacyProcess candidacyProcess) {
        this.candidacyProcess = candidacyProcess;
    }

}
