from arbol import Arbol

nombres = [
    "Juan", "Pedro", "Ana", "Luis", "Carlos", "Beatriz", "María", "Fernando",
    "Ricardo", "Sofía", "David", "Elena", "Gabriel", "Hugo", "Isabel", "Javier",
    "Karla", "Laura", "Manuel", "Natalia", "Óscar", "Patricia", "Raúl", "Silvia",
    "Tomás", "Úrsula", "Víctor", "Ximena", "Yolanda", "Zacarías"
]

arbol = Arbol()
for nombre in nombres:
    arbol.insertar(nombre)

print("Árbol en orden:")
arbol.imprimirArbol()

print()

print("\nBuscar 'Javier':", arbol.buscarNodo("Javier"))
print("Buscar 'Ñoño':", arbol.buscarNodo("Ñoño"))

print()

arbol.vaciar()

print()

print("Árbol en orden:")
arbol.imprimirArbol()
print()