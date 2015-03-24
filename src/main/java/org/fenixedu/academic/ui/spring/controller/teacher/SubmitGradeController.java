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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;

import org.fenixedu.academic.domain.Evaluation;
import org.fenixedu.academic.domain.Professorship;
import org.fenixedu.academic.domain.exceptions.DomainException;
import org.fenixedu.academic.service.services.exceptions.FenixServiceException;
import org.fenixedu.academic.service.services.exceptions.FenixServiceMultipleException;
import org.fenixedu.academic.service.services.teacher.WriteMarks;
import org.fenixedu.academic.service.services.teacher.WriteMarks.StudentMark;
import org.fenixedu.academic.ui.struts.action.teacher.ManageExecutionCourseDA;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.RedirectView;

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
        model.addAttribute("action", "/teacher/evaluation/" + executionCourse.getExternalId() + "/" + evaluation.getExternalId()
                + "/submitGradeFile");
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

    private Map<String, String> loadMarks(final InputStream inputStream) throws IOException {
        final Map<String, String> marks = new HashMap<String, String>();

        final InputStreamReader input = new InputStreamReader(inputStream);
        final BufferedReader reader = new BufferedReader(input);

        char[] buffer = new char[4096];
        StringBuilder fileContents = new StringBuilder();
        int i = 0;
        while ((i = reader.read(buffer)) != -1) {
            fileContents.append(buffer, 0, i);
        }

        try {
            final StringTokenizer stringTokenizer = new StringTokenizer(fileContents.toString());
            while (true) {
                String studentNumber = getNextToken(stringTokenizer);
                if (studentNumber == null) {
                    return marks;
                }

                String mark = getNextToken(stringTokenizer);
                if (mark == null) {
                    throw new Exception();
                }
                marks.put(studentNumber, mark);
            }
        } catch (Exception e) {
            throw new IOException("error.file.badFormat");
        }
    }

    private String getNextToken(StringTokenizer stringTokenizer) {
        while (stringTokenizer.hasMoreTokens()) {
            String token = stringTokenizer.nextToken().trim();
            if (token.length() > 0) {
                return token;
            }
        }
        return null;
    }

    public boolean loadMarks(SubmitGradeBean gradeFileBean) {

        final MultipartFile fileItem = gradeFileBean.getGradeFile();
        InputStream inputStream = null;
        try {
            inputStream = fileItem.getInputStream();
            final Map<String, String> marks = loadMarks(inputStream);

            WriteMarks.writeByStudent(executionCourse.getExternalId(), evaluation.getExternalId(), buildStudentMarks(marks));

            return true;

        } catch (FenixServiceMultipleException e) {
            for (DomainException domainException : e.getExceptionList()) {
                //addErrorMessage(BundleUtil.getString(Bundle.APPLICATION, domainException.getKey(), domainException.getArgs()));
            }
            return false;
        } catch (IOException e) {
            //addErrorMessage(BundleUtil.getString(Bundle.APPLICATION, e.getMessage()));
            return false;
        } catch (FenixServiceException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {

                }
            }
        }
    }

    private List<StudentMark> buildStudentMarks(Map<String, String> marks) {
        final List<StudentMark> result = new ArrayList<StudentMark>();
        for (final Entry<String, String> entry : marks.entrySet()) {
            result.add(new StudentMark(entry.getKey(), entry.getValue()));
        }
        return result;
    }

    @RequestMapping(value = "/submitGradeFile", method = RequestMethod.POST)
    public RedirectView submitGrades(Model model, @ModelAttribute("gradeBean") @Validated SubmitGradeBean gradeFileBean,
            BindingResult bindingResult) {
        loadMarks(gradeFileBean);
        return null;
    }
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