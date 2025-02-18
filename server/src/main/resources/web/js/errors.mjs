import {queryString} from "./url.mjs";
import {MISSING_CREDENTIALS, INVALID_CREDENTIALS, USER_ALREADY_EXISTS} from "../js_gen/error_codes.mjs";

export function showErrorInField(fieldId) {
    const query = queryString();
    const errorCode = parseInt(query['error']);

    if (isNaN(errorCode)) {
        return;
    }

    /** @type {?string} */
    let error = null;
    const errorElement = document.getElementById(fieldId);
    switch (errorCode) {
        case MISSING_CREDENTIALS: {
            error = 'No se han enviado credenciales. Error interno, contacte con el administrador'
            break;
        }
        case INVALID_CREDENTIALS: {
            error = 'Las credenciales no son válidas. El nombre de usuario debe tener más de 3 caracteres, y la contraseña más de 6.'
            break;
        }
        case USER_ALREADY_EXISTS: {
            error = 'El usuario ya existe'
            break;
        }
        default: {
            console.warn('Unknown error', errorCode);
            break
        }
    }
    if (error !== null) {
        errorElement.innerText = error;
        errorElement.style.display = 'block';
    } else {
        errorElement.innerText = '';
        errorElement.style.display = 'none';
    }
}
