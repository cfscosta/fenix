/*
 * Created on 13/Jul/2004
 *
 */
package net.sourceforge.fenixedu.dataTransferObject;

import net.sourceforge.fenixedu.domain.IExamStudentRoom;

/**
 * @author T�nia Pous�o
 *  
 */
public class InfoExamStudentRoomWithInfoStudentAndInfoRoom extends InfoExamStudentRoom {

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.fenixedu.dataTransferObject.InfoObject#copyFromDomain(Dominio.IDomainObject)
     */
    public void copyFromDomain(IExamStudentRoom examStudentRoom) {
        super.copyFromDomain(examStudentRoom);
        if (examStudentRoom != null) {
            setInfoStudent(InfoStudentWithInfoPerson.newInfoFromDomain(examStudentRoom.getStudent()));
            setInfoRoom(InfoRoom.newInfoFromDomain(examStudentRoom.getRoom()));
        }
    }

    public static InfoExamStudentRoom newInfoFromDomain(IExamStudentRoom examStudentRoom) {
        InfoExamStudentRoomWithInfoStudentAndInfoRoom infoExamStudentRoom = null;
        if (examStudentRoom != null) {
            infoExamStudentRoom = new InfoExamStudentRoomWithInfoStudentAndInfoRoom();
            infoExamStudentRoom.copyFromDomain(examStudentRoom);
        }
        return infoExamStudentRoom;
    }
}