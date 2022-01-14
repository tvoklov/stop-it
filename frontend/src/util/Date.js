export function toGoodFormat(date) {
    const day = date.getDay()
    const month = date.getMonth()
    const year = date.getYear()

    return (day < 10 ? "0" + day : day) + " " + (month < 10 ? "0" + month : month) + " " + year
}