import { param } from 'jquery'

export function fetchFromApi(path, params) {
    return fetch('/api' + path, {...params, });
}

export function postToApi(path, body, params) {
    return fetch('/api' + path, {...params, ...{
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(body)
    }});
}

export function putToApi(path, body, params) {
    return fetch('/api' + path, {...param, ...{
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(body)
    }});
}