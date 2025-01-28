@file:JsModule("./autofill.mjs")

package compatibility

external class AutofillField : JsAny {
    constructor(name: String, type: String, autocomplete: String, onChange: (String) -> Unit)
}

external fun autofillCompatibilityLayer(fields: JsArray<AutofillField>)
