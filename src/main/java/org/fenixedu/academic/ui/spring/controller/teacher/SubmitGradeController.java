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
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.UriBuilder;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.fenixedu.academic.domain.Evaluation;
import org.fenixedu.academic.domain.Professorship;
import org.fenixedu.academic.domain.accessControl.PersistentStudentGroup;
import org.fenixedu.academic.service.services.exceptions.FenixServiceException;
import org.fenixedu.academic.service.services.teacher.WriteMarks;
import org.fenixedu.academic.service.services.teacher.WriteMarks.StudentMark;
import org.fenixedu.academic.ui.struts.action.teacher.ManageExecutionCourseDA;
import org.fenixedu.academic.util.Bundle;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.i18n.BundleUtil;
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

import pt.ist.fenixWebFramework.servlets.filters.contentRewrite.GenericChecksumRewriter;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

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
        model.addAttribute("gradeBean", new SubmitGradeBean());
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

    private Map<String, String> loadMarks(final InputStream inputStream, List<String> errors) throws IOException {
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
                String error = checkStudentGradePair(studentNumber, mark);
                if (error == null) {
                    marks.put(studentNumber, mark);
                } else {
                    errors.add(error);
                }
            }
        } catch (Exception e) {
            throw new IOException("error.file.badFormat");
        }
    }

    String checkStudentGradePair(String student, String grade) {
        List<String> errors = WriteMarks.verifyStudent(executionCourse, student);
        if (errors != null && errors.size() > 0) {
            return errors.stream().collect(Collectors.joining("\n"));
        }
        if (!checkGrade(grade)) {
            return BundleUtil.getString(Bundle.APPLICATION, "errors.invalidMark", grade, student);
        }
        return null;
    }

    boolean checkStudent(String student) {
        User user = User.findByUsername(student);
        if (user == null) {
            return false;
        }
        boolean exists = false;
        user.getPerson().getStudent();
        for (PersistentStudentGroup psg : executionCourse.getStudentGroupSet()) {
            if (psg.getMembers().contains(user)) {
                exists = true;
            }
        }
        return exists;
    }

    boolean checkGrade(String mark) {
        if (mark.trim().isEmpty()) {
            return true;
        }
        return evaluation.getGradeScale().belongsTo(mark);
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

    public Map<String, String> loadMarks(SubmitGradeBean gradeFileBean, List<String> errors) {

        final MultipartFile fileItem = gradeFileBean.getGradeFile();
        InputStream inputStream = null;
        try {
            inputStream = fileItem.getInputStream();
            final Map<String, String> marks = loadMarks(inputStream, errors);
            return marks;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
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

    @RequestMapping(value = "/submitVerified", method = RequestMethod.POST)
    private RedirectView sibmitVerified(Model model, @ModelAttribute("gradeBean") @Validated SubmitGradeBean gradeFileBean,
            BindingResult bindingResult, HttpSession session, HttpServletRequest request) {

        try {
            WriteMarks.writeByStudent(executionCourse.getExternalId(), evaluation.getExternalId(),
                    buildStudentMarks(gradeFileBean.getMarks()));
        } catch (FenixServiceException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String editMarksListUrl =
                UriBuilder.fromUri("/teacher/evaluation/editMarksList.faces")
                        .queryParam("executionCourseID", executionCourse.getExternalId())
                        .queryParam("evaluationID", evaluation.getExternalId()).build().toString();
        String sendEmailWithChecksumUrl =
                GenericChecksumRewriter.injectChecksumInUrl(request.getContextPath(), editMarksListUrl, session);
        return new RedirectView(sendEmailWithChecksumUrl, true);
    }

    @RequestMapping(value = "/submitGradeFileExcel", method = RequestMethod.POST)
    public String submitGradesExcel(Model model, @ModelAttribute("gradeBean") @Validated SubmitGradeBean gradeFileBean,
            BindingResult bindingResult, HttpSession session, HttpServletRequest request) {
        List<String> errors = new ArrayList<String>();
        JsonElement fileAsStrings = processExcelMarks(gradeFileBean, errors);

        model.addAttribute("errors", errors.stream().distinct().collect(Collectors.toList()));
        model.addAttribute("actionResubmit",
                "/teacher/evaluation/" + executionCourse.getExternalId() + "/" + evaluation.getExternalId()
                        + "/submitGradeFileExcel");
        model.addAttribute("excelRepresent", fileAsStrings.toString());
        model.addAttribute("gradeBean", gradeFileBean);
        model.addAttribute("executionCourse", executionCourse);
        model.addAttribute("evaluation", evaluation);
        model.addAttribute("actionSubmit",
                "/teacher/evaluation/" + executionCourse.getExternalId() + "/" + evaluation.getExternalId() + "/submitVerified");
        return "teacher/selectMarks";
    }

    private JsonElement processExcelMarks(SubmitGradeBean gradeFileBean, List<String> errors) {
        JsonObject MapOfMaps = new JsonObject();
        try {
            InputStream inp = gradeFileBean.getGradeFile().getInputStream();
            Workbook wb = null;
            wb = WorkbookFactory.create(inp);
            Sheet sheet = wb.getSheetAt(0);
            Row titleRow = sheet.getRow(0);
            for (int titles = 1; titles < titleRow.getLastCellNum(); titles++) {
                JsonObject StudentToGrade = new JsonObject();
                for (int i = 1; i < sheet.getLastRowNum() + 1; i++) {
                    Row row = sheet.getRow(i);
                    String student = getCellStringValue(row.getCell(0));
                    String grade = getCellStringValue(row.getCell(titles));
                    String error = checkStudentGradePair(student, grade);
                    if (error == null) {
                        StudentToGrade.addProperty(student, grade);
                    } else {
                        errors.add(error);
                    }
                }
                MapOfMaps.add(getCellStringValue(titleRow.getCell(titles)), StudentToGrade);
            }
        } catch (IOException | InvalidFormatException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return MapOfMaps;
    }

    private String getCellStringValue(Cell cell) {
        if (cell == null) {
            return "";
        }
        int type = cell.getCellType();
        if (type == Cell.CELL_TYPE_FORMULA) {
            type = cell.getCachedFormulaResultType();
        }
        switch (type) {
        case Cell.CELL_TYPE_NUMERIC: {
            return "" + (int) cell.getNumericCellValue();
        }
        case Cell.CELL_TYPE_STRING: {
            return "" + cell.getStringCellValue();
        }
        case Cell.CELL_TYPE_BOOLEAN: {
            return "" + cell.getBooleanCellValue();
        }
        case Cell.CELL_TYPE_ERROR:
        case Cell.CELL_TYPE_BLANK:
        }
        return "";
    }

    @RequestMapping(value = "/submitGradeFileTXT", method = RequestMethod.POST)
    public String submitGradesTXT(Model model, @ModelAttribute("gradeBean") @Validated SubmitGradeBean gradeFileBean,
            BindingResult bindingResult, HttpSession session, HttpServletRequest request) {
        List<String> errors = new ArrayList<>();
        Map<String, String> marks = loadMarks(gradeFileBean, errors);

        gradeFileBean.setMarks(marks);
        model.addAttribute("gradeBean", gradeFileBean);
        model.addAttribute("errors", errors.stream().distinct().collect(Collectors.toList()));
        model.addAttribute("actionResubmit",
                "/teacher/evaluation/" + executionCourse.getExternalId() + "/" + evaluation.getExternalId()
                        + "/submitGradeFileTXT");
        model.addAttribute("executionCourse", executionCourse);
        model.addAttribute("evaluation", evaluation);
        model.addAttribute("actionSubmit",
                "/teacher/evaluation/" + executionCourse.getExternalId() + "/" + evaluation.getExternalId() + "/submitVerified");
        return "teacher/previewMarks";
    }

}