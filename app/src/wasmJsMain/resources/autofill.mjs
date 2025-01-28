/**
 * @callback AutofillOnChange
 * @param {string} value
 */

/**
 * @typedef {Object} AutofillField
 * @prop {string} name
 * @prop {'text'|'password'|'email'} type
 * @prop {AutoFill|null} autocomplete
 * @prop {AutofillOnChange} onChange
 */

export class AutofillField {
    constructor(name, type, autocomplete, onChange) {
        this.name = name
        this.type = type
        this.autocomplete = autocomplete
        this.onChange = onChange
    }
}

/**
 * Brings autofill compatibility for compose.
 * @param {AutofillField[]} fields
 */
export function autofillCompatibilityLayer(fields) {
    // Remove the form if already exists
    document.getElementById('autofillForm')?.remove();

    // Create the new form
    const form = document.createElement("form");
    form.id = 'autofillForm';
    for (const data of fields) {
        const field = document.createElement("input");
        field.type = data.type;
        field.name = data.name;
        field.id = `autofill-${data.name}`;
        field.className = 'autofill';
        if (data.autocomplete != null) field.autocomplete = data.autocomplete;
        field.addEventListener('input', (event) => {
            data.onChange(field.value);
        });
        form.append(field);
    }
    document.body.append(form)
}
