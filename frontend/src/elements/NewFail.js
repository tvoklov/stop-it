import React, { useState } from "react"
import { postToApi } from "../util/Fetching"

export function NewFail({ onFailReport }) {
    const [fail, setFail] = useState({})

    const handleFailReport = () => {
        postToApi("/fail/new", fail, {}).then(() => {
            setFail({})
            onFailReport()
        })
    }

    return (
        <div>
            <form>
                <div className="grid-container">
                    <section className="medium-6 cell">
                        <label htmlFor="reason">Reason</label>
                        <input
                            type="text" id="reason" name="reason"
                            value={fail.reason ? fail.reason : ""}
                            onChange={({ target }) => setFail(p => ({ ...p, reason: target.value }))}
                        />
                    </section>

                    <section className="medium-6 cell">
                        <label htmlFor="toWhat">What did it?</label>
                        <input
                            type="text" id="toWhat" name="toWhat"
                            value={fail.toWhat ? fail.toWhat : ""}
                            onChange={({ target }) => setFail(p => ({ ...p, toWhat: target.value }))}
                        />
                    </section>

                    <section className="medium-6 cell">
                        <input
                            id="satisfied" type="checkbox"
                            value={fail.satisfied ? fail.satisfied : ""}
                            onChange={({ target }) => setFail(p => ({ ...p, satisfied: target.checked }))}
                        />
                        <label htmlFor="satisfied">Did you even like it?</label>
                    </section>

                    <button className="submit alert button" type="button" onClick={handleFailReport}>I fucked up</button>
                </div>
            </form>
        </div>
    )
}