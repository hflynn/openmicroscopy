package xmlMVC;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import javax.swing.JComponent;
import javax.swing.JPanel;

// the in-memory form of an xml element
// has hash map of attributes, plus FormField and FieldEditor panels to display them

public class DataField {
	
	// attribute types
	// changes to the attributes are reflected in XML element saving.
	// any significant changes should be given a new XML version number (in XMLModel)

	public static final String ELEMENT_NAME = "elementName";
	public static final String DESCRIPTION = "description";
	public static final String VALUE = "value";
	public static final String TEXT_NODE_VALUE ="textNodeValue";
	public static final String DEFAULT = "default";
	// public static final String DERIVED_FROM = "derived_from";
	public static final String PROTOCOL_FILE_NAME = "protocolFileName";
	public static final String INPUT_TYPE = "inputType";
	public static final String DROPDOWN_OPTIONS = "dropdownOptions";
	public static final String TABLE_COLUMN_NAMES = "tableColumnNames";
	public static final String UNITS = "units";
	public static final String KEYWORDS = "keywords";
	public static final String SUBSTEPS_COLLAPSED ="substepsCollapsed"; // "true" or "false"
	public static final String URL = "url";
	public static final String PRO_HAS_EXP_CHILDREN = "protocolHasExpChildren";
	//public static final String[] ALL_ATTRIBUTES = 
	//{NAME, DESCRIPTION, VALUE, DEFAULT, PROTOCOL_FILE_NAME, INPUT_TYPE, DROPDOWN_OPTIONS,
	//	UNITS, KEYWORDS, SUBSTEPS_COLLAPSED, URL, PRO_HAS_EXP_CHILDREN};
	
	// attribute values
	public static final String TRUE = "true";
	public static final String FALSE = "false";
	
	// input types
	public static final String PROTOCOL_TITLE = "Protocol Title";
	public static final String FIXED_PROTOCOL_STEP = "Fixed Step";
	public static final String TEXT_ENTRY_STEP = "Text";
	public static final String MEMO_ENTRY_STEP = "Text Box";
	public static final String NUMBER_ENTRY_STEP = "Number";
	public static final String DROPDOWN_MENU_STEP = "Drop-down Menu";
	public static final String DATE = "Date";
	public static final String TABLE = "Table";
	public static final String CUSTOM = "Custom";
	public static final String[] INPUT_TYPES = 
	{FIXED_PROTOCOL_STEP, TEXT_ENTRY_STEP,
	MEMO_ENTRY_STEP, DROPDOWN_MENU_STEP, NUMBER_ENTRY_STEP, DATE, TABLE};
	
//	 the names used for the UI - MUST be in SAME ORDER as INPUT_TYPES they correspond to 
	// this means you can change the UI names without changing INPUT_TYPES.
	public static final String[] UI_INPUT_TYPES = 	
	{ "Fixed", "Text", "Text Box", "Drop-down Menu", "Number", "Date", "Table"};
	
	// Datafield has attributes stored in LinkedHashMap
	LinkedHashMap<String, String> allAttributesMap;
	
	// the two JPanels that display the dataField, and hold optional attributes
	FormField formField;
	FieldEditor fieldEditor;
	
	// the node of the dataField tree structure that holds this datafield
	DataFieldNode node;
	
	// the factory that makes fieldEditor and formField subclasses depending on input type
	static FieldEditorFormFieldFactory factory = FieldEditorFormFieldFactory.getInstance();

	
	public DataField(DataFieldNode node) {
		this.node = node;

		allAttributesMap = new LinkedHashMap<String, String>();
		
		// default type
		setAttribute(DataField.INPUT_TYPE, DataField.FIXED_PROTOCOL_STEP, false);

		// blank formField and fieldEditor (not used factory)
		formField = new FormField(this);
	}
	
	public DataField(LinkedHashMap<String, String> allAttributesMap, DataFieldNode node) {
		
		this.node = node;
		
		this.allAttributesMap = allAttributesMap;
		
		//factory passes (this) dataField to these constructors
		factory = FieldEditorFormFieldFactory.getInstance();
		formField = factory.getFormField(this);
	}
	
	// returns a duplicate dataField
	public DataField(DataField dataFieldToCopy, DataFieldNode node) {
		
		this.node = node;
		
//		 get all attributes of the original datafield, make a copy
		LinkedHashMap<String, String> allAttributes = dataFieldToCopy.getAllAttributes();
		this.allAttributesMap = new LinkedHashMap<String, String>(allAttributes);

		//factory passes (this) dataField to these constructors
		factory = FieldEditorFormFieldFactory.getInstance();
		formField = factory.getFormField(this);
	}
	
	public void setAttribute(String name, String value, boolean notifyObservers) {
		// System.out.println("DataField.setAttribute(notifyObservers="+ notifyObservers +"): " + name + "=" + value);
		
		allAttributesMap.put(name, value);
		if (notifyObservers) notifyDataFieldObservers();
	}
	
	
	
	public String getAttribute(String name) {
		return allAttributesMap.get(name);
	}
	
	public void changeDataFieldInputType(String newInputType) {
		
		// don't allow changing to "Custom" from another Input type
		// only allowed to change new dataFields to "custom" when importing
		if((newInputType.equals(DataField.CUSTOM)) && (getAttribute(DataField.INPUT_TYPE)!= null)
				&& (!getAttribute(DataField.INPUT_TYPE).equals(DataField.CUSTOM))) return;
		
		
		// delete all attributes other than name & desc. (not relevant to new input type - probably)
		// unless external element ("Custom" field), which may have many attrbutes
		if (!newInputType.equals(DataField.CUSTOM)) {
			
//			 keep the original name & description (may be null)
			String copyName = allAttributesMap.get(DataField.ELEMENT_NAME);
			String copyDescription = allAttributesMap.get(DataField.DESCRIPTION);
			
			allAttributesMap.clear();
			
			setAttribute(DataField.ELEMENT_NAME, copyName, false);
			setAttribute(DataField.DESCRIPTION, copyDescription, false);
		}
		
		setAttribute(DataField.INPUT_TYPE, newInputType, false);
		
		// factory makes new subclasses of formField and fieldEditor based on inputType
		fieldEditor = factory.getFieldEditor(this);
		formField = factory.getFormField(this);
		
		// reset the blue colour
		node.nodeClicked(true);
		
		// this only needed if call comes from FieldEditor (let fieldEditor do it)
		// notifyDataFieldObservers(); 
	}
	
	// export method. return a LinkedHashMap with all name-value 
	public LinkedHashMap<String, String> getAllAttributes() {

		return allAttributesMap;
	}

	// called when changes are made to allAttributesMap
	public void notifyDataFieldObservers() {
		formField.dataFieldUpdated();
		if (fieldEditor != null) fieldEditor.dataFieldUpdated();
		node.dataFieldUpdated();
	}
	
	public void setHighlighted(boolean highlighted) {
		formField.setHighlighted(highlighted);
	}
	
	public JPanel getFieldEditor() {
		if (fieldEditor == null) {
			fieldEditor = factory.getFieldEditor(this);
		}
		return fieldEditor;
	}
	
	public JPanel getFormField() {
		return formField;
	}
	
	public String getName() {
		if ((getAttribute(DataField.ELEMENT_NAME) != null) && (getAttribute(DataField.ELEMENT_NAME).length() > 0))
			return getAttribute(DataField.ELEMENT_NAME);
		else return "Name";		// have to return SOME text or formField panel may be v.v.small!
	}
	//	 used to update dataField etc when fieldEditor panel is edited
	public void setName(String newName, boolean notifyObservers) {
		setAttribute(DataField.ELEMENT_NAME, newName, notifyObservers);
	}
	
	public String getDescription() {
		return getAttribute(DataField.DESCRIPTION);
	}
	// used to update dataField etc when fieldEditor panel is edited
	public void setDescription(String newDescription) {
		setAttribute(DataField.DESCRIPTION, newDescription, true);
	}
	
	public String getURL() {
		return getAttribute(DataField.URL);
	}
	
	public void setURL(String url) {
		setAttribute(DataField.URL, url, false);
	}
	
	public void copyDefaultValueToInputField() {
		formField.setValue(getAttribute(DataField.DEFAULT));
	}
	
	public String getInputType() {
		return getAttribute(DataField.INPUT_TYPE);
	}
	
	public DataFieldNode getNode() {
		return node;
	}
	
	public boolean hasChildren() {
		boolean hasChildren = (!(getNode().children.isEmpty()));
		return hasChildren;
	}

	public void refreshTitleCollapsed() {
		formField.refreshTitleCollapsed();
	}
	public void setExperimentalEditing(boolean enabled) {
		formField.setExperimentalEditing(enabled);
	}
	public ArrayList<JComponent> getVisibleAttributes() {
		return formField.getVisibleAttributes();
	}
	public void formFieldClicked(boolean clearOthers) {
		node.nodeClicked(clearOthers);
	}
	public void hideChildren(boolean hidden) {
		node.hideChildren(hidden);
	}

}
