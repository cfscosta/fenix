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
package org.fenixedu.academic.ui.spring.controller.teacher;

import org.fenixedu.academic.domain.Evaluation;
import org.fenixedu.academic.domain.Professorship;
import org.fenixedu.academic.ui.struts.action.teacher.ManageExecutionCourseDA;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/teacher/evaluation/{executionCourse}/{evaluation}")
public class SubmitGradeController extends ExecutionCourseController {

    @Autowired
    StudentGroupService studentGroupService;

    Evaluation evaluation;

    @ModelAttribute("Evaluation")
    public Evaluation getGrouping(@PathVariable Evaluation evaluation) {
        this.evaluation = evaluation;
        return evaluation;
    }

    @RequestMapping(method = RequestMethod.GET)
    public String home(Model model) {
        model.addAttribute("executionCourse", executionCourse);
        model.addAttribute("evaluation", evaluation);
        return "teacher/loadMarks";
    }

    @Override
    protected Class<?> getFunctionalityType() {
        return ManageExecutionCourseDA.class;
    }

    @Override
    Boolean getPermission(Professorship prof) {
        return prof.getPermissions().getGroups();
    }

//    @ModelAttribute("Evaluation")
//    public Evaluation getGrouping(@PathVariable Evaluation evaluation) {
//        this.evaluation = evaluation;
//        return evaluation;
//    }
//
//    @RequestMapping(value = "/createStudentGroup", method = RequestMethod.POST)
//    public AbstractUrlBasedView createStudentGroup(Model model, @ModelAttribute("addStudent") @Validated AttendsBean addStudents,
//            BindingResult bindingResult) {
//        return null;
//    }
//
//    @RequestMapping(value = "/sendEmail/{studentGroup}", method = RequestMethod.GET)
//    public RedirectView sendEmail(Model model, HttpServletRequest request, HttpSession session,
//            @PathVariable StudentGroup studentGroup) {
//        String label =
//                studentGroup.getGrouping().getName() + "-" + BundleUtil.getString(Bundle.APPLICATION, "label.group")
//                        + studentGroup.getGroupNumber();
//
//        ArrayList<Recipient> recipients = new ArrayList<Recipient>();
//        recipients.add(Recipient.newInstance(
//                label,
//                UserGroup.of(studentGroup.getAttendsSet().stream().map(Attends::getRegistration).map(Registration::getPerson)
//                        .map(Person::getUser).collect(Collectors.toSet()))));
//        String sendEmailUrl =
//                UriBuilder
//                        .fromUri("/messaging/emails.do")
//                        .queryParam("method", "newEmail")
//                        .queryParam("sender", ExecutionCourseSender.newInstance(executionCourse).getExternalId())
//                        .queryParam("recipient", recipients.stream().filter(r -> r != null).map(r -> r.getExternalId()).toArray())
//                        .build().toString();
//        String sendEmailWithChecksumUrl =
//                GenericChecksumRewriter.injectChecksumInUrl(request.getContextPath(), sendEmailUrl, session);
//        return new RedirectView(sendEmailWithChecksumUrl, true);
//
//    }
}