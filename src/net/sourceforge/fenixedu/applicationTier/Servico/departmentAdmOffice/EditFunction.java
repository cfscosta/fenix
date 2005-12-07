/*
 * Created on Oct 28, 2005
 *	by mrsp
 */
package net.sourceforge.fenixedu.applicationTier.Servico.departmentAdmOffice;

import java.util.Date;

import net.sourceforge.fenixedu.applicationTier.Servico.exceptions.FenixServiceException;
import net.sourceforge.fenixedu.domain.exceptions.DomainException;
import net.sourceforge.fenixedu.domain.organizationalStructure.Function;
import net.sourceforge.fenixedu.domain.organizationalStructure.IFunction;
import net.sourceforge.fenixedu.domain.organizationalStructure.IPersonFunction;
import net.sourceforge.fenixedu.domain.organizationalStructure.PersonFunction;
import net.sourceforge.fenixedu.persistenceTier.ExcepcaoPersistencia;
import net.sourceforge.fenixedu.persistenceTier.ISuportePersistente;
import net.sourceforge.fenixedu.persistenceTier.PersistenceSupportFactory;
import pt.utl.ist.berserk.logic.serviceManager.IService;

public class EditFunction implements IService {

    public void run(Integer personFunctionID, Integer functionID, Date beginDate, Date endDate,
            Double credits) throws ExcepcaoPersistencia, FenixServiceException, DomainException {

        ISuportePersistente suportePersistente = PersistenceSupportFactory
                .getDefaultPersistenceSupport();

        IPersonFunction person_Function = (IPersonFunction) suportePersistente.getIPersistentObject()
                .readByOID(PersonFunction.class, personFunctionID);

        if (person_Function == null) {
            throw new FenixServiceException("error.no.personFunction");
        }

        IFunction function = (IFunction) suportePersistente.getIPersistentObject().readByOID(
                Function.class, functionID);

        if (function == null) {
            throw new FenixServiceException("erro.noFunction");
        }

        person_Function.edit(function, beginDate, endDate, credits);
    }
}
