package net.sourceforge.fenixedu.renderers;

/**
 * 
 * @author pcma
 */
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import net.sourceforge.fenixedu.renderers.components.HtmlCheckBox;
import net.sourceforge.fenixedu.renderers.components.HtmlComponent;
import net.sourceforge.fenixedu.renderers.components.HtmlContainer;
import net.sourceforge.fenixedu.renderers.components.HtmlInlineContainer;
import net.sourceforge.fenixedu.renderers.components.HtmlMultipleHiddenField;
import net.sourceforge.fenixedu.renderers.components.HtmlText;
import net.sourceforge.fenixedu.renderers.components.controllers.HtmlController;
import net.sourceforge.fenixedu.renderers.components.converters.Converter;
import net.sourceforge.fenixedu.renderers.components.state.IViewState;
import net.sourceforge.fenixedu.renderers.layouts.Layout;
import net.sourceforge.fenixedu.renderers.layouts.TabularLayout;
import net.sourceforge.fenixedu.renderers.model.MetaObject;
import net.sourceforge.fenixedu.renderers.model.MetaObjectCollection;
import net.sourceforge.fenixedu.renderers.model.MetaObjectFactory;
import net.sourceforge.fenixedu.renderers.model.MetaSlot;
import net.sourceforge.fenixedu.renderers.schemas.Schema;
import net.sourceforge.fenixedu.renderers.utils.RenderKit;

import org.apache.commons.beanutils.BeanComparator;

public class TabularOptionInputRenderer extends InputRenderer {

    private String providerClass;

    private String classes;

    private String eachSchema;

    private DataProvider provider;

    private String eachLayout;

    private String sortBy;

    /**
     * 
     * @property
     */
    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sort) {
        sortBy = sort;
    }

    /**
     * 
     * @property
     */

    public String getEachLayout() {
        return eachLayout;
    }

    public void setEachLayout(String layout) {
        eachLayout = layout;
    }

    /**
     * 
     * @property
     */

    public String getClasses() {
        return classes;
    }

    public void setClasses(String classes) {
        this.classes = classes;
    }

    /**
     * 
     * @property
     */

    public String getProviderClass() {
        return providerClass;
    }

    public void setProviderClass(String providerClass) {
        this.providerClass = providerClass;
    }

    /**
     * 
     * @property
     */

    public String getEachSchema() {
        return eachSchema;
    }

    public void setEachSchema(String schema) {
        this.eachSchema = schema;
    }

    protected DataProvider getProvider() {
        if (this.provider == null) {
            String className = getProviderClass();

            try {
                Class providerCass = (Class<DataProvider>) Class.forName(className);
                this.provider = (DataProvider) providerCass.newInstance();
            } catch (Exception e) {
                throw new RuntimeException("could not get a data provider instance", e);
            }
        }

        return this.provider;
    }

    protected Converter getConverter() {
        DataProvider provider = getProvider();

        return provider.getConverter();
    }

    protected Collection getPossibleObjects() {
        Object object = getInputContext().getParentContext().getMetaObject().getObject();
        Object value = getInputContext().getMetaObject().getObject();

        if (getProviderClass() != null) {
            try {
                DataProvider provider = getProvider();
                Collection collection = (Collection) provider.provide(object, value);
                if (getSortBy() != null) {
                    Collections.sort((List) collection, new BeanComparator(getSortBy()));
                }
                return collection;
            } catch (Exception e) {
                throw new RuntimeException("exception while executing data provider", e);
            }
        } else {
            throw new RuntimeException("a data provider must be supplied");
        }
    }

    @Override
    protected Layout getLayout(Object object, Class type) {

        final List<MetaObject> metaObjects = (List<MetaObject>) getMetaObjects(getPossibleObjects());
        final Collection objectsReceived = (Collection) object;

        final HtmlMultipleHiddenField hiddenField = new HtmlMultipleHiddenField();
        hiddenField.bind((MetaSlot) getContext().getMetaObject());
        hiddenField.setConverter(getConverter());

        return new TabularLayout() {

            private List<HtmlCheckBox> checkboxes = new ArrayList<HtmlCheckBox>();

            @Override
            public HtmlComponent createLayout(Object object, Class type) {
                HtmlContainer container = new HtmlInlineContainer();
                HtmlComponent component = super.createLayout(object, type);
                container.addChild(hiddenField);
                container.addChild(component);

                hiddenField.setController(new HtmlController() {

                    @Override
                    public void execute(IViewState viewState) {
                        List<String> values = new ArrayList<String>();

                        for (HtmlCheckBox checkBox : checkboxes) {
                            if (checkBox.isChecked()) {
                                values.add(checkBox.getValue());
                            }
                        }

                        hiddenField.setValues(values.toArray(new String[0]));
                    }
                });

                return container;
            }

            @Override
            protected boolean hasHeader() {
                return metaObjects.size() > 0;
            }

            @Override
            protected HtmlComponent getHeaderComponent(int columnIndex) {
                String text = "";
                if (columnIndex != 0) {
                    text = metaObjects.get(0).getSlots().get(columnIndex - 1).getLabel();
                }
                return new HtmlText(text, false);
            }

            @Override
            protected int getNumberOfColumns() {
                if (metaObjects.size() > 0) {
                    MetaObject metaObject = metaObjects.get(0);
                    return metaObject.getSlots().size() + 1; // +1 due to the
                    // checkbox
                } else {
                    return 0;
                }
            }

            @Override
            protected int getNumberOfRows() {
                return metaObjects.size();
            }

            @Override
            protected HtmlComponent getComponent(int rowIndex, int columnIndex) {

                if (columnIndex == 0) {
                    HtmlCheckBox checkBox = new HtmlCheckBox();
                    this.checkboxes.add(checkBox);

                    MetaObject metaObject = metaObjects.get(rowIndex);
                    checkBox.setUserValue(metaObject.getKey().toString());

                    checkBox.setName(hiddenField.getName() + "/" + metaObject.getKey().toString());

                    if (objectsReceived != null && objectsReceived.contains(metaObject.getObject())) {
                        checkBox.setChecked(true);
                    }
                    return checkBox;
                } else {
                    MetaSlot slot = getSlotUsingName(metaObjects.get(rowIndex), columnIndex - 1);
                    slot.setReadOnly(true);
                    return renderSlot(slot);
                }
            }

            protected MetaSlot getSlotUsingName(MetaObject object, int columnIndex) {
                MetaObject referenceObject = metaObjects.get(0);
                MetaSlot referenceSlot = referenceObject.getSlots().get(columnIndex);

                MetaSlot directSlot = object.getSlots().get(columnIndex); // common
                // case
                if (directSlot.getName().equals(referenceSlot.getName())) {
                    return directSlot;
                }

                for (MetaSlot slot : object.getSlots()) {
                    if (slot.getName().equals(referenceSlot.getName())) {
                        return slot;
                    }
                }

                return null;
            }

        };
    }

    private List<MetaObject> getMetaObjects(Collection collection) {
        List<MetaObject> metaObjects = new ArrayList<MetaObject>();

        MetaObject contextMetaObject = getContext().getMetaObject();
        if (contextMetaObject instanceof MetaObjectCollection) {
            // reuse meta objects
            MetaObjectCollection multipleMetaObject = (MetaObjectCollection) getContext()
                    .getMetaObject();

            for (Object object : collection) {
                for (MetaObject metaObject : multipleMetaObject.getAllMetaObjects()) {
                    if (object.equals(metaObject.getObject())) {
                        metaObjects.add(metaObject);
                        break;
                    }
                }
            }
        } else {

            Schema schema = null;
            if (eachSchema == null) {
                schema = RenderKit.getInstance().findSchema(getContext().getSchema());
            } else {
                schema = RenderKit.getInstance().findSchema(eachSchema);
            }
            for (Object object : collection) {
                metaObjects.add(MetaObjectFactory.createObject(object, schema));
            }
        }

        return metaObjects;
    }

}
