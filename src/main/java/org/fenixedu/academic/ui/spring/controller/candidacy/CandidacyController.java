/**
 * Copyright © 2002 Instituto Superior Técnico
 *
 * This file is part of FenixEdu Academic.
 *
 * FenixEdu Academic is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FenixEdu Academic is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with FenixEdu Academic.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.fenixedu.academic.ui.spring.controller.candidacy;

import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.UriBuilder;

import org.fenixedu.academic.domain.ExecutionInterval;
import org.fenixedu.academic.domain.candidacyProcess.CandidacyProcess;
import org.fenixedu.academic.domain.candidacyProcess.IndividualCandidacyProcess;
import org.fenixedu.academic.domain.period.CandidacyPeriod;
import org.fenixedu.academic.domain.period.CandidacyProcessCandidacyPeriod;
import org.fenixedu.academic.ui.spring.controller.AcademicAdministrationSpringApplication;
import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.view.RedirectView;

import pt.ist.fenixWebFramework.servlets.filters.contentRewrite.GenericChecksumRewriter;
import pt.ist.fenixframework.Atomic;

@SpringFunctionality(app = AcademicAdministrationSpringApplication.class, title = "label.candidacy.move.candidate")
@RequestMapping("/candidacies")
public class CandidacyController {

    @RequestMapping(method = RequestMethod.GET)
    public String home(Model model) {
        return "fenixedu-academic/candidacies/move";
    }

    @RequestMapping(value = "/moveCandidate/{process}", method = RequestMethod.GET)
    public String moveCandidate(@PathVariable IndividualCandidacyProcess process, Model model) {
        ExecutionInterval executionInterval = process.getCandidacyProcess().getCandidacyPeriod().getExecutionInterval();
        CandidacyProcessCandidacyPeriod candidacyPeriod = process.getCandidacyProcess().getCandidacyPeriod();
        List<? extends CandidacyPeriod> cpp = executionInterval.getCandidacyPeriods(candidacyPeriod.getClass());
        model.addAttribute("processes",
                cpp.stream().map(cp -> (CandidacyProcessCandidacyPeriod) cp)
                        .flatMap(cp -> cp.getCandidacyProcessesSet().stream()).collect(Collectors.toList()));
        MoveCandidacyBean moveCandidacyBean = new MoveCandidacyBean();
        moveCandidacyBean.setCandidacy(process);
        model.addAttribute("moveCandidacyBean", moveCandidacyBean);
        model.addAttribute("action", "/candidacies/moveCandidate/");
        return "fenixedu-academic/candidacies/move";
    }

    @RequestMapping(value = "/moveCandidate/{process}", method = RequestMethod.POST)
    public RedirectView moveCandidate(@ModelAttribute MoveCandidacyBean process, Model model, HttpSession session,
            HttpServletRequest request) {
        setCandidacyProcess(process.getCandidacy(), process.getCandidacyProcess());
        String checkProcessUrl =
                UriBuilder
                        .fromUri(
                                "/academicAdministration/caseHandling" + process.getCandidacy().getClass().getSimpleName()
                                        + ".do").queryParam("method", "listProcesses")
                        .queryParam("parentProcessId", process.getCandidacy().getCandidacyProcess().getExternalId()).build()
                        .toString();
        String checkProcessWithChecksumUrl =
                GenericChecksumRewriter.injectChecksumInUrl(request.getContextPath(), checkProcessUrl, session);
        return new RedirectView(checkProcessWithChecksumUrl, true);
    }

    @Atomic
    private void setCandidacyProcess(IndividualCandidacyProcess individualProcess, CandidacyProcess candidacyProcess) {
        individualProcess.setCandidacyProcess(candidacyProcess);
    }

}